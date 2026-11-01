<?php

const BACKENDS = [
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

if (!preg_match('#/api/(movie-service|oscar-service)(/.*)$#', $_SERVER['REQUEST_URI'], $match)) {
    fail(404, 'Неизвестный сервис');
}
[, $service, $path] = $match;

$method = $_SERVER['REQUEST_METHOD'];
$override = strtoupper($_SERVER['HTTP_X_HTTP_METHOD_OVERRIDE'] ?? '');
if ($method === 'POST' && in_array($override, ['PATCH', 'DELETE'], true)) {
    $method = $override;
}

$headers = [];
foreach (['CONTENT_TYPE' => 'Content-Type', 'HTTP_ACCEPT' => 'Accept'] as $key => $name) {
    if (isset($_SERVER[$key])) {
        $headers[] = "$name: {$_SERVER[$key]}";
    }
}

$context = stream_context_create([
    'http' => [
        'method' => $method,
        'header' => $headers,
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

$body = @file_get_contents(BACKENDS[$service] . $path, false, $context);
if ($body === false) {
    fail(503, "Сервис $service недоступен");
}

foreach ($http_response_header as $index => $line) {
    if ($index === 0) {
        http_response_code((int) explode(' ', $line)[1]);
    } elseif (stripos($line, 'Content-Type:') === 0) {
        header($line);
    }
}
echo $body;
