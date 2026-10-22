// Адреса сервисов. По умолчанию — пути на том же origin, что и фронтенд:
// в разработке их проксирует Vite (vite.config.js), на сервере — nginx.
// Переопределяются при сборке: VITE_MOVIE_SERVICE_URL, VITE_OSCAR_SERVICE_URL.
export const MOVIE_SERVICE_URL = import.meta.env.VITE_MOVIE_SERVICE_URL ?? "/api/movie-service"
export const OSCAR_SERVICE_URL = import.meta.env.VITE_OSCAR_SERVICE_URL ?? "/api/oscar-service"
