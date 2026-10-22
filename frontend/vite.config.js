import path from 'node:path'
import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

/**
 * Прокси к сервисам на Payara: браузер ходит на тот же origin, поэтому CORS не нужен,
 * а самоподписанные сертификаты доменов проверяет не браузер, а прокси (secure: false).
 * Пути /movies и /oscars заняты страницами фронтенда — запросы к API идут через префикс /api/<сервис>.
 */
function service(target) {
  return {
    target,
    secure: false,
    changeOrigin: true,
    rewrite: (url) => url.replace(/^\/api\/[^/]+/, ''),
    // сервис не запущен — ответ в формате ошибки спецификации, как у сервиса за недоступной зависимостью
    configure: (proxy) => proxy.on('error', (error, req, res) => {
      if (res.headersSent || res.writableEnded) return
      res.writeHead(503, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ message: `Сервис ${target} недоступен`, timestamp: new Date().toISOString() }))
    }),
  }
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(import.meta.dirname, './src'),
    },
  },
  server: {
    proxy: {
      '/api/movie-service': service(process.env.MOVIE_SERVICE_URL ?? 'https://localhost:8181'),
      '/api/oscar-service': service(process.env.OSCAR_SERVICE_URL ?? 'https://localhost:10081'),
    },
  },
})
