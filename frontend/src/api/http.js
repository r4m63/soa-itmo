// Заготовка HTTP-слоя: пока запросы не отправляются, а только описываются и отвечают моками.
// TODO: заменить mockResponse на fetch(baseUrl + path, ...) и бросать ApiError(response.status, await response.json())
// для ответов 4xx/5xx.

const MOCK_DELAY_MS = 300

/** Печатает запрос, который ушёл бы в сервис, и возвращает результат мока с задержкой. */
export async function mockResponse(baseUrl, method, path, produce) {
  console.debug(`[api] ${method} ${baseUrl}${path}`)
  await new Promise((resolve) => setTimeout(resolve, MOCK_DELAY_MS))
  return produce()
}
