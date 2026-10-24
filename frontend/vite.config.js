import path from 'node:path'
import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

function service(target) {
  return {
    target,
    secure: false,
    changeOrigin: true,
    rewrite: (url) => url.replace(/^\/api\/[^/]+/, ''),
    configure: (proxy) => proxy.on('error', (error, req, res) => {
      if (res.headersSent || res.writableEnded) return
      res.writeHead(503, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ message: `Сервис ${target} недоступен`, timestamp: new Date().toISOString() }))
    }),
  }
}

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
