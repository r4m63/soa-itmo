// Адреса сервисов. Задаются при сборке: VITE_MOVIE_SERVICE_URL, VITE_OSCAR_SERVICE_URL.
export const MOVIE_SERVICE_URL = import.meta.env.VITE_MOVIE_SERVICE_URL ?? "https://localhost:8181"
export const OSCAR_SERVICE_URL = import.meta.env.VITE_OSCAR_SERVICE_URL ?? "https://localhost:8281"
