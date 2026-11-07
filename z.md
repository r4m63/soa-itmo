# TLS в SOA на Helios

Три HTTPS-соединения защищают разные участки: браузер -> Helios, PHP -> Movie/Oscar и Oscar -> Movie. У каждого
соединения свой клиент, сервер, проверка сертификата и сеансовые ключи. Сертификат сайта `se.ifmo.ru` принадлежит
Helios. Самоподписанные сертификаты Movie и Oscar принадлежат доменам Payara.

```text
Браузер -- HTTPS, сертификат Helios --> Apache Helios
                                           |
                                           | /api/* -> api.php
                                           v
                                      PHP-прокси
                                       |       |
                     HTTPS, Movie cert |       | HTTPS, Oscar cert
                                       v       v
                                   Movie     Oscar
                                     ^          |
                                     +-- HTTPS -+  Oscar доверяет сертификату Movie
```

----------------------------------------------------------------------------------------------------

## TLS и SSL

**TLS** - протокол между HTTP и транспортом. Для обычного HTTPS сначала устанавливается TCP-соединение, затем
проходит TLS handshake, после него HTTP-запросы идут внутри защищённого канала. TLS проверяет сторону соединения,
шифрует данные и обнаруживает их изменение в пути.

**SSL** - старое семейство протоколов и привычное название в терминах вроде SSL-сертификата. В проекте используется
TLS. `https://` означает HTTP поверх TLS.

**Handshake** - обмен, в котором стороны согласуют алгоритмы и временные ключи. Сервер предъявляет сертификат,
клиент проверяет его, а сервер доказывает владение закрытым ключом. После этого запросы шифруются сеансовыми ключами.

**Сеансовый ключ** - временный симметричный ключ конкретного соединения. Ключ из сертификата не шифрует каждый байт
HTTP. Для HTTP/3 транспорт другой: TLS встроен в QUIC, а не расположен поверх TCP.

----------------------------------------------------------------------------------------------------

## Ключи, подпись и сертификат

**Симметричное шифрование** - обе стороны используют общий секрет для шифрования и расшифровки. Оно быстрое, поэтому
им защищён поток HTTP после handshake.

**Асимметричная пара** - закрытый и открытый ключи. Закрытый ключ остаётся у владельца. Открытый ключ можно
распространять. Подпись создают закрытым ключом, а проверяют открытым. Проверка не создаёт вторую подпись:
`Verify(открытый ключ, данные, подпись)` возвращает результат проверки.

**Сертификат X.509** - документ с открытым ключом, именем узла, сроком действия, издателем и подписью издателя.
Закрытого ключа в сертификате нет. Имя `localhost` у Movie и Oscar указано в SAN, а не только в CN.

Подпись создают из определённой части сертификата, называемой `TBSCertificate`. Её подписывает закрытым ключом
издатель. Клиент проверяет подпись открытым ключом издателя. Если изменить имя или открытый ключ в сертификате,
проверка подписи не пройдёт.

```text
Издатель: Sign(закрытый ключ издателя, данные сертификата) -> подпись
Клиент:   Verify(открытый ключ издателя, данные, подпись) -> true/false
```

Проверка подписи доказывает связь данных с ключом издателя. Она сама по себе не отвечает на вопрос, доверять ли
издателю. Для этого у клиента должно быть заранее установленное доверие.

**Самоподписанный сертификат** - сертификат, который владелец подписал собственным закрытым ключом. У Movie и Oscar
`Owner` и `Issuer` совпадают. Любой способен создать свой самоподписанный сертификат с именем `localhost`, поэтому
PHP и Oscar заранее хранят правильные публичные сертификаты.

**CA** - центр сертификации, подписывающий сертификаты других участников. У обычного публичного сайта цепочка идёт
от сертификата сайта через промежуточный CA к корневому CA, которому клиент уже доверяет. Корневой сертификат тоже
часто самоподписан. Ему доверяют из-за наличия в доверенном хранилище, а не из-за самоподписи.

----------------------------------------------------------------------------------------------------

## Где лежат ключи и доверие

**Keystore** - файл собственных ключей и сертификатов. **Truststore** - файл доверенных сертификатов для исходящих
соединений. Это роли файлов, а не разные форматы. Здесь оба имеют формат PKCS#12 (`.p12`). Payara держит их в
`config/` каждого домена. JVM умеет читать эти файлы, а `keytool` из JDK позволяет ими управлять.

| Участник      | Собственный ключ и сертификат                                                          | Доверенный сертификат                                       |
|---------------|----------------------------------------------------------------------------------------|-------------------------------------------------------------|
| Movie         | `~/payara7/glassfish/domains/movie-service/config/keystore.p12`, alias `movie-service` | Для этой связи Oscar ему не нужен                           |
| Oscar         | `~/payara7/glassfish/domains/oscar-service/config/keystore.p12`, alias `oscar-service` | Movie в `config/cacerts.p12`, alias `movie-service`         |
| PHP           | Собственного ключа для вызова Java-сервисов нет                                        | `~/public_html/soa/movie-service.crt` и `oscar-service.crt` |
| Apache Helios | Сертификатом и ключом `se.ifmo.ru` управляет администрация                             | Браузер проверяет цепочку сертификата сайта                 |

В `keytool -list` запись `PrivateKeyEntry` содержит закрытый ключ с сертификатом. `trustedCertEntry` содержит
только публичный сертификат. В действующих keystore alias `movie-service` и `oscar-service` имеют тип
`PrivateKeyEntry`, а запись `movie-service` в Oscar `cacerts.p12` - `trustedCertEntry`. Стандартные записи Payara
`s1as` также существуют, но HTTPS-листенеры выбрали именные alias.

**Команды для просмотра:**

```bash
# Все записи Movie. keytool спросит пароль хранилища.
keytool -list -keystore ~/payara7/glassfish/domains/movie-service/config/keystore.p12

# Собственный ключ и сертификат Oscar.
keytool -list -v -alias oscar-service \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/keystore.p12

# Доверенный сертификат Movie у Oscar.
keytool -list -v -alias movie-service \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/cacerts.p12
```

Пароль текущих хранилищ - `changeit`. Без `-storepass` он вводится интерактивно и не остаётся в истории shell.
На Helios используй настроенную login shell (`bash -l`): она ограничивает память Java для `keytool` и `asadmin`.

----------------------------------------------------------------------------------------------------

## Как проходит запрос

1. Браузер открывает `https://se.ifmo.ru/~t409659/soa/` и проверяет сертификат сайта Helios. Apache отдаёт HTML и JS.
2. Фронтенд делает `GET /~t409659/soa/api/movie-service/movies`. Этот запрос защищён TLS браузер -> Helios.
3. Apache расшифровывает запрос. Правило в `frontend/helios/.htaccess` направляет `/api/*` в `api.php`.
4. PHP убирает внешний префикс `/api/movie-service` и обращается к `https://localhost:24081/movies`.
5. Payara предъявляет сертификат Movie. PHP проверяет его по `movie-service.crt` и проверяет имя `localhost`.
6. Payara передаёт `/movies` контроллеру Movie. Ответ идёт обратно через PHP и Apache.

Для `/api/oscar-service` PHP проверяет `oscar-service.crt` и обращается к порту `24181`. Если операция Oscar читает
или меняет фильмы, Oscar отдельно подключается к Movie по HTTPS. Сертификат Movie он проверяет по своему truststore.
Ни один закрытый ключ при этих вызовах не передаётся. На каждом участке TLS создаётся свой сеансовый ключ.

Публичный Helios может блокировать прямой `PATCH` до PHP. Поэтому фронтенд посылает `POST` с
`X-HTTP-Method-Override: PATCH` или `DELETE`, а PHP восстанавливает метод перед отправкой в Payara. Проверку
допустимости операции выполняет Java-сервис.

----------------------------------------------------------------------------------------------------

## Как создали и назначили сертификаты

Команды ниже описывают **воспроизводимую схему текущей конфигурации**, а не дословный журнал первоначальной настройки.
Старый `README.md` показывает alias `s1as`, тогда как действующие HTTPS-листенеры используют `movie-service` и
`oscar-service`. Повторное создание ключа заменит сертификат и потребует обновить доверенные копии у всех клиентов.

**Генерация новой пары и самоподписанного сертификата:**

```bash
keytool -genkeypair -alias movie-service -keyalg RSA -keysize 2048 -validity 3650 \
  -dname "CN=localhost, OU=SOA, O=ITMO, L=Saint Petersburg, C=RU" \
  -ext san=dns:localhost,ip:127.0.0.1 \
  -keystore ~/payara7/glassfish/domains/movie-service/config/keystore.p12

keytool -genkeypair -alias oscar-service -keyalg RSA -keysize 2048 -validity 3650 \
  -dname "CN=localhost, OU=SOA, O=ITMO, L=Saint Petersburg, C=RU" \
  -ext san=dns:localhost,ip:127.0.0.1 \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/keystore.p12
```

Эти команды нужны при создании нового хранилища или после осознанной замены существующего alias. На работающем
сервере alias уже существуют. Перед заменой ключей останови домены и сохрани резервные копии обоих `.p12`.

**HTTPS-листенеры:**

```bash
# У каждого домена свой alias сертификата.
~/payara7/bin/asadmin --port 24048 set \
  configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.cert-nickname=movie-service
~/payara7/bin/asadmin --port 24148 set \
  configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.cert-nickname=oscar-service

# Незашифрованные HTTP-листенеры выключены.
~/payara7/bin/asadmin --port 24048 set \
  configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.enabled=false
~/payara7/bin/asadmin --port 24148 set \
  configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.enabled=false
```

Проверить значение `cert-nickname` и состояние листенера можно теми же путями, заменив `set` на `get` и убрав
`=значение`. HTTPS-порты - `24081` и `24181`, HTTP-порты `24080` и `24180` выключены.

----------------------------------------------------------------------------------------------------

## Как Oscar доверяет Movie

Oscar вызывает `https://localhost:24081`. В его `domain.xml` есть JVM options
`javax.net.ssl.trustStore=${com.sun.aas.instanceRoot}/config/cacerts.p12` и
`javax.net.ssl.keyStore=${com.sun.aas.instanceRoot}/config/keystore.p12`. Payara передаёт их JVM при запуске домена.
Публичный сертификат Movie импортирован в Oscar truststore.

**Экспорт и импорт при первичной настройке:**

```bash
keytool -exportcert -rfc -alias movie-service \
  -keystore ~/payara7/glassfish/domains/movie-service/config/keystore.p12 \
  -file /tmp/movie-service.crt

keytool -importcert -alias movie-service -file /tmp/movie-service.crt \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/cacerts.p12

keytool -list -v -alias movie-service \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/cacerts.p12
```

`keytool -importcert` не надо повторять при уже существующем alias. При смене сертификата сначала сравни отпечатки,
затем замени доверенную запись и перезапусти Oscar. Обратный импорт Oscar в Movie не нужен.

----------------------------------------------------------------------------------------------------

## Как PHP доверяет сервисам

`./gradlew deploy-frontend` экспортирует сертификаты из двух keystore Payara в
`~/public_html/soa/movie-service.crt` и `oscar-service.crt`. Файл `frontend/helios/api.php` выбирает нужный
сертификат через `cafile` и проверяет имя `localhost`:

```php
'ssl' => [
    'cafile' => __DIR__ . "/$service.crt",
    'peer_name' => 'localhost',
    'verify_peer' => true,
    'verify_peer_name' => true,
],
```

Отключать `verify_peer` или `verify_peer_name` нельзя: тогда прокси перестанет удостоверять сервис. В
`frontend/helios/.htaccess` правило `<Files "*.crt"> Require all denied </Files>` запрещает скачивать эти файлы по
HTTP. PHP продолжает читать их локально. В `public_html` не кладут закрытые ключи или файлы `.p12`.

**Ручной экспорт публичных сертификатов:**

```bash
cd ~/public_html/soa
keytool -exportcert -rfc -alias movie-service \
  -keystore ~/payara7/glassfish/domains/movie-service/config/keystore.p12 \
  -file movie-service.crt
keytool -exportcert -rfc -alias oscar-service \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/keystore.p12 \
  -file oscar-service.crt
```

На практике эти команды выполняет Gradle-задача `deploy-frontend`. Публичные `.crt` не секретны, но их соответствие
текущим ключам Payara важно. После замены ключа сервисов обнови копии сертификатов и доверие Oscar.

----------------------------------------------------------------------------------------------------

## Проверка и диагностика

**Записи и отпечатки:**

```bash
keytool -list -v -alias movie-service \
  -keystore ~/payara7/glassfish/domains/movie-service/config/keystore.p12
keytool -list -v -alias movie-service \
  -keystore ~/payara7/glassfish/domains/oscar-service/config/cacerts.p12
openssl x509 -in ~/public_html/soa/movie-service.crt \
  -noout -subject -issuer -dates -fingerprint -sha256 -ext subjectAltName
```

Отпечатки Movie в keystore Movie, truststore Oscar и `.crt` PHP должны совпасть. Для Oscar сравни его keystore и
`~/public_html/soa/oscar-service.crt`. `Owner = Issuer` показывает самоподпись, но не доказывает доверие клиента.

**Проверка TLS и API без отключения проверки сертификата:**

```bash
openssl s_client -connect localhost:24081 -servername localhost \
  -verify_hostname localhost -CAfile ~/public_html/soa/movie-service.crt </dev/null
openssl s_client -connect localhost:24181 -servername localhost \
  -verify_hostname localhost -CAfile ~/public_html/soa/oscar-service.crt </dev/null
curl --cacert ~/public_html/soa/movie-service.crt https://localhost:24081/movies
```

Успешный OpenSSL выводит `Verify return code: 0 (ok)`. Без `-CAfile` самоподписанный сертификат может вызвать
ошибку доверия. `curl -k` отключает проверку, поэтому подходит только для изоляции проблемы, а не для доказательства
правильно настроенного TLS.

**Состояние Payara:**

```bash
~/payara7/bin/asadmin list-domains
~/payara7/bin/asadmin --port 24048 list-applications
~/payara7/bin/asadmin --port 24148 list-applications
```

Если PHP возвращает `503`, проверь запуск обоих доменов, HTTPS-порты, сертификат в `public_html` и SAN `localhost`.
Если Oscar возвращает `503` при вызове Movie, проверь сертификат Movie в Oscar `cacerts.p12` и URL
`MOVIE_SERVICE_URL=https://localhost:24081`. Изменения keystore и truststore требуют перезапуска домена, чтобы JVM
прочитала обновлённые файлы.

----------------------------------------------------------------------------------------------------

## Команды keytool

- `-genkeypair` создаёт пару ключей и сертификат.
- `-list -v` показывает alias, тип записи, срок действия, SAN и отпечаток.
- `-exportcert -rfc` выгружает только публичный сертификат в PEM.
- `-importcert` добавляет сертификат в доверенное хранилище.
- `-delete -alias ...` удаляет запись. Перед удалением проверь тип: `PrivateKeyEntry` содержит закрытый ключ.

`-rfc` даёт текстовый PEM с `BEGIN CERTIFICATE`. Без него `keytool -exportcert` пишет двоичный DER. Оба формата
содержат публичный сертификат. `openssl x509` читает PEM непосредственно, а DER - с `-inform DER`.

----------------------------------------------------------------------------------------------------

## Открытые вопросы по REST и Spring

В исходном файле были заголовки без содержания. Это задачи для отдельных заметок, а не главы про TLS:

- Разработка RESTful сервисов: языки, фреймворки и библиотеки.
- Java EE и JAX-RS: ресурсы, основные и вспомогательные аннотации.
- Spring Web MVC REST: отличие от JAX-RS, DispatcherServlet, HandlerMapping, ViewResolver, аннотации и JSON.
- Spring Data REST: экспорт репозиториев, типы ресурсов, автоматическое описание и сравнение с ручным API.
