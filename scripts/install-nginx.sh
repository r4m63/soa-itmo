#!/bin/sh
# Собирает nginx из исходников и ставит его в ~/.local без root (FreeBSD).
# Раскладка как у helios-container: всё в ~/.local/nginx, команда-обёртка
# в ~/.local/bin/nginx, PATH блоком в ~/.profile (оригинал в profile.before).
#
#   sh install-nginx.sh [--port PORT] [--jobs N] [--sources DIR]
#   sh install-nginx.sh uninstall --yes
#
# --sources DIR: взять nginx-$NGINX_VERSION.tar.gz и pcre2-$PCRE2_VERSION.tar.gz
# из каталога вместо скачивания (если у сервера нет доступа в интернет).
set -eu

NGINX_VERSION=1.30.5
NGINX_SHA256=6c20565aa2325cb82216ae804f4a4ff1875179014759a381c42ddc8e11c4906d
PCRE2_VERSION=10.49
PCRE2_SHA256=929f0b20e62879252a15886b06c89f1edef61a363cbd5826fb041080a5e557ae

base=$HOME/.local/nginx
bin=$HOME/.local/bin
launcher=$bin/nginx
owner=$base/.nginx-local
marker='# nginx-local managed launcher'
begin='# >>> nginx-local >>>'
end='# <<< nginx-local <<<'

die() {
    echo "$*" >&2
    exit 1
}

usage() {
    echo 'install-nginx.sh [--port PORT] [--jobs N] [--sources DIR]'
    echo 'install-nginx.sh uninstall --yes'
}

no_link() {
    [ ! -L "$1" ] || die "$1 не должен быть ссылкой"
}

download() {
    if [ -n "$sources" ]; then
        [ -f "$sources/${1##*/}" ] || die "Нет файла $sources/${1##*/}"
        cp "$sources/${1##*/}" "$2"
    elif command -v curl >/dev/null 2>&1; then
        curl --proto '=https' --proto-redir '=https' -fL --retry 3 --max-time 300 -o "$2" "$1"
    else
        fetch -o "$2" "$1"
    fi
}

verify() {
    [ "$(sha256 -q "$1")" = "$2" ] || die "SHA-256 не совпадает: $1"
}

# Печатает .profile без нашего блока; код 1, если маркеры не парные.
strip_profile() {
    awk -v b="$begin" -v e="$end" '
        $0 == b { skip = 1; nb++; next }
        $0 == e { skip = 0; ne++; next }
        !skip { print }
        END { if (nb != ne || skip) exit 1 }
    ' "$1"
}

profile_rewrite() {
    p=$HOME/.profile
    no_link "$p"
    [ -f "$p" ] || : >"$p"
    if [ "$1" = add ] && [ ! -e "$base/profile.before" ]; then
        cp -p "$p" "$base/profile.before"
    fi
    tmp_profile=$(mktemp "$HOME/.profile.XXXXXXXX")
    strip_profile "$p" >"$tmp_profile" || {
        rm -f "$tmp_profile"
        die 'Повреждён блок nginx-local в .profile'
    }
    if [ "$1" = add ]; then
        cat >>"$tmp_profile" <<EOF
$begin
case ":\$PATH:" in *":\$HOME/.local/bin:"*) ;; *) export PATH="\$HOME/.local/bin:\$PATH" ;; esac
$end
EOF
    fi
    chmod 600 "$tmp_profile"
    mv "$tmp_profile" "$p"
}

write_conf() {
    cat >"$base/conf/nginx.conf" <<EOF
worker_processes 2;
error_log logs/error.log;
pid logs/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include mime.types;
    default_type application/octet-stream;
    access_log logs/access.log;
    sendfile on;
    keepalive_timeout 65;

    server {
        listen 127.0.0.1:$port;
        server_name localhost;

        location / {
            root html;
            index index.html;
        }
    }
}
EOF
}

install_nginx() {
    [ "$(uname -s)" = FreeBSD ] || die 'Нужна FreeBSD.'
    for tool in cc tar awk sha256; do
        command -v "$tool" >/dev/null 2>&1 || die "Не найдено: $tool"
    done
    [ -f /usr/include/openssl/ssl.h ] || die 'Нет заголовков OpenSSL в /usr/include'
    [ -f /usr/include/zlib.h ] || die 'Нет /usr/include/zlib.h'
    case $port in
        '' | *[!0-9]*) die "Некорректный порт: $port" ;;
    esac
    [ "$port" -ge 1024 ] && [ "$port" -le 65535 ] || die 'Порт должен быть в диапазоне 1024–65535'
    if sockstat -4 -6 -l -P tcp 2>/dev/null | awk 'NR > 1 { print $6 }' | grep -q "[:.]$port\$"; then
        echo "Внимание: порт $port уже занят, поменяйте listen в $base/conf/nginx.conf" >&2
    fi
    make_cmd=make
    command -v gmake >/dev/null 2>&1 && make_cmd=gmake

    umask 077
    mkdir -p "$HOME/.local"
    for path in "$HOME/.local" "$base" "$bin" "$launcher"; do
        no_link "$path"
    done
    if [ -e "$launcher" ] && ! grep -qF "$marker" "$launcher"; then
        die "$launcher не принадлежит nginx-local"
    fi
    if [ -d "$base" ] && [ ! -f "$owner" ] && [ -n "$(ls -A "$base")" ]; then
        die "$base создан не этим скриптом, установка остановлена"
    fi

    tmp=$(mktemp -d "$HOME/.local/nginx-build.XXXXXXXX")
    trap 'rm -rf -- "$tmp"' EXIT HUP INT TERM
    download "https://nginx.org/download/nginx-$NGINX_VERSION.tar.gz" "$tmp/nginx.tar.gz"
    download "https://github.com/PCRE2Project/pcre2/releases/download/pcre2-$PCRE2_VERSION/pcre2-$PCRE2_VERSION.tar.gz" "$tmp/pcre2.tar.gz"
    verify "$tmp/nginx.tar.gz" "$NGINX_SHA256"
    verify "$tmp/pcre2.tar.gz" "$PCRE2_SHA256"
    tar -xzf "$tmp/nginx.tar.gz" -C "$tmp"
    tar -xzf "$tmp/pcre2.tar.gz" -C "$tmp"

    fresh_conf=1
    [ -f "$base/conf/nginx.conf" ] && fresh_conf=0
    (
        cd "$tmp/nginx-$NGINX_VERSION"
        ./configure \
            --prefix="$base" \
            --with-pcre="$tmp/pcre2-$PCRE2_VERSION" \
            --with-pcre-jit \
            --with-threads \
            --with-http_ssl_module \
            --with-http_v2_module \
            --with-http_realip_module \
            --with-http_gzip_static_module \
            --with-http_stub_status_module \
            --with-stream \
            --with-stream_ssl_module
        "$make_cmd" -j "$jobs"
        "$make_cmd" install
    )
    : >"$owner"
    # Стандартный конфиг слушает 80-й порт, без root его не открыть.
    # Свой конфиг пишем только при первой установке, чтобы не затереть правки.
    [ "$fresh_conf" = 1 ] && write_conf

    mkdir -p "$bin"
    tmp_launcher=$(mktemp "$bin/.nginx.XXXXXXXX")
    cat >"$tmp_launcher" <<EOF
#!/bin/sh
$marker
exec "$base/sbin/nginx" "\$@"
EOF
    chmod 700 "$tmp_launcher"
    mv "$tmp_launcher" "$launcher"

    profile_rewrite add
    "$base/sbin/nginx" -v
    "$base/sbin/nginx" -t
    echo "Готово. Выполните: . ~/.profile"
    echo "Запуск: nginx  · перезагрузка: nginx -s reload  · остановка: nginx -s stop"
    echo "Конфиг: $base/conf/nginx.conf (listen 127.0.0.1:$(awk '$1 == "listen" { sub(/;$/, "", $2); print $2; exit }' "$base/conf/nginx.conf" | sed 's/.*://'))"
}

uninstall_nginx() {
    [ "${1:-}" = --yes ] || die 'uninstall --yes удалит ~/.local/nginx вместе с конфигом, сайтом и логами'
    if [ -d "$base" ] && [ ! -f "$owner" ]; then
        die "$base создан не этим скриптом, удаление остановлено"
    fi
    if [ -f "$base/logs/nginx.pid" ] && [ -x "$base/sbin/nginx" ]; then
        "$base/sbin/nginx" -s stop 2>/dev/null || true
    fi
    if [ -f "$launcher" ] && grep -qF "$marker" "$launcher"; then
        rm -f "$launcher"
    fi
    if [ -f "$HOME/.profile" ]; then
        profile_rewrite remove
    fi
    rm -rf -- "$base"
    echo 'nginx удалён. Откройте новую сессию, чтобы обновить PATH.'
}

case "${1:-}" in
    uninstall)
        shift
        uninstall_nginx "$@"
        exit 0
        ;;
    install) shift ;;
esac

# Порт по умолчанию зависит от uid, чтобы реже пересекаться с соседями по серверу.
port=$((20000 + $(id -u) % 20000))
jobs=$(sysctl -n hw.ncpu 2>/dev/null || echo 2)
sources=
[ "$jobs" -le 8 ] || jobs=8
while [ $# -gt 0 ]; do
    case $1 in
        --port)
            port=${2:?Нет значения --port}
            shift 2
            ;;
        --jobs)
            jobs=${2:?Нет значения --jobs}
            shift 2
            ;;
        --sources)
            sources=${2:?Нет значения --sources}
            shift 2
            ;;
        -h | --help)
            usage
            exit 0
            ;;
        *) die "Неизвестный параметр: $1" ;;
    esac
done
install_nginx
