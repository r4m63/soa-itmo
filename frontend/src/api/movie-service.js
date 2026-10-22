// Movie Service (JAX-RS, Payara №1). Функции повторяют операции docs/movie-service.yaml.

import { buildMovieListParams } from "@/lib/movie-query"
import { MOVIE_SERVICE_URL } from "./config"
import { request } from "./http"

/** GET /movies?filter&sort&page&size → MoviePage | 400. */
export function listMovies({ filters, sorts, page, size }) {
  const params = buildMovieListParams({ filters, sorts, page, size })
  return request(MOVIE_SERVICE_URL, "GET", `/movies?${params}`)
}

/** GET /movies/{id} → Movie | 404. */
export function getMovie(id) {
  return request(MOVIE_SERVICE_URL, "GET", `/movies/${id}`)
}

/** POST /movies (MovieInput) → 201 Movie | 400. */
export function createMovie(input) {
  return request(MOVIE_SERVICE_URL, "POST", "/movies", input)
}

/** PATCH /movies/{id} (MoviePatch: только изменённые поля, null — очистить) → Movie | 400 | 404. */
export function patchMovie(id, patch) {
  return request(MOVIE_SERVICE_URL, "PATCH", `/movies/${id}`, patch)
}

/** DELETE /movies/{id} → 204 | 404. */
export function deleteMovie(id) {
  return request(MOVIE_SERVICE_URL, "DELETE", `/movies/${id}`)
}

/** DELETE /movies/by-genre/{genre} → DeletionResult. Фильмы без жанра не удаляются. */
export function deleteMoviesByGenre(genre) {
  return request(MOVIE_SERVICE_URL, "DELETE", `/movies/by-genre/${encodeURIComponent(genre)}`)
}

/**
 * GET /movies/{genres|mpaa-ratings|colors|countries}?search → { items }.
 * Поиск на стороне сервиса: contains без учёта регистра.
 */
export function searchDictionary(dictionary, search) {
  const query = search ? `?search=${encodeURIComponent(search)}` : ""
  return request(MOVIE_SERVICE_URL, "GET", `/movies/${dictionary}${query}`)
}
