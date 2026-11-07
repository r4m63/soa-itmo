<?php

$backends = [
    'movie-service' => 'https://localhost:24081',
    'oscar-service' => 'https://localhost:24181',
];

function fail(int $status, string $message): never
{
    http_response_code($status);
    header('Content-Type: application/json');
    echo json_encode(['message' => $message, 'timestamp' => gmdate('Y-m-d\TH:i:s\Z')], JSON_UNESCAPED_UNICODE);
    exit;
}

if (!preg_match('#/api/(movie-service|oscar-service)(/[^?]*)?(?:\?(.*))?$#', $_SERVER['REQUEST_URI'], $match)) {
    fail(404, 'Неизвестный сервис');
}

$service = $match[1];
$path = $match[2] ?? '';
if ($path === '') {
    $path = '/';
}
if (isset($match[3])) {
    $path .= '?' . $match[3];
}
$method = $_SERVER['REQUEST_METHOD'];
// helios блокирует прямой PATCH до php, поэтому фронт отправляет POST с этим хедером
// подмену разрешаем только для POST и только на PATCH/DELETE, ?? '' покрывает отсутствие заголовка
if ($method === 'POST' && in_array($_SERVER['HTTP_X_HTTP_METHOD_OVERRIDE'] ?? '', ['PATCH', 'DELETE'], true)) {
    // до сервиса должен дойти настоящий PATCH/DELETE, чтобы сработал нужный endpoint
    $method = $_SERVER['HTTP_X_HTTP_METHOD_OVERRIDE'];
}

$context = stream_context_create([
    'http' => [
        'method' => $method,
        'header' => isset($_SERVER['CONTENT_TYPE']) ? 'Content-Type: ' . $_SERVER['CONTENT_TYPE'] : '',
        // сырое тело входящего запроса
        'content' => file_get_contents('php://input'),
        'ignore_errors' => true,
        'timeout' => 30,
    ],
    'ssl' => [
        'cafile' => __DIR__ . "/$service.crt",
        'peer_name' => 'localhost',
        'verify_peer' => true,
        'verify_peer_name' => true,
    ],
]);

$body = @file_get_contents($backends[$service] . $path, false, $context);
if ($body === false) {
    fail(503, "Сервис $service недоступен");
}

foreach ($http_response_header as $index => $line) {
    if ($index === 0) {
        http_response_code((int)explode(' ', $line)[1]);
    } elseif (stripos($line, 'Content-Type:') === 0) {
        header($line);
    }
}
echo $body;
