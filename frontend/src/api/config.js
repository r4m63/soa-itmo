const API_URL = `${import.meta.env.BASE_URL}api`

export const MOVIE_SERVICE_URL = import.meta.env.VITE_MOVIE_SERVICE_URL ?? `${API_URL}/movie-service`
export const OSCAR_SERVICE_URL = import.meta.env.VITE_OSCAR_SERVICE_URL ?? `${API_URL}/oscar-service`
