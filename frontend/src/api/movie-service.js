// Movie Service (JAX-RS, Payara №1). Функции повторяют операции docs/movie-service.yaml.

import { ApiError } from "@/lib/api-errors"
import { enumValues } from "@/lib/enums"
import { buildMovieListParams } from "@/lib/movie-query"
import { MOVIE_SERVICE_URL } from "./config"
import { mockResponse } from "./http"
import { mockMovies } from "./mock-data"

let movies = structuredClone(mockMovies)
let nextId = movies.length + 1

/** GET /movies?filter&sort&page&size → MoviePage. Мок: фильтры и сортировку не применяет, только режет страницу. */
export function listMovies({ filters, sorts, page, size }) {
  const params = buildMovieListParams({ filters, sorts, page, size })
  return mockResponse(MOVIE_SERVICE_URL, "GET", `/movies?${params}`, () => ({
    items: movies.slice(page * size, (page + 1) * size),
    page,
    size,
    totalElements: movies.length,
    totalPages: Math.ceil(movies.length / size),
  }))
}

/** GET /movies/{id} → Movie | 404. */
export function getMovie(id) {
  return mockResponse(MOVIE_SERVICE_URL, "GET", `/movies/${id}`, () => findOrThrow(id))
}

/** POST /movies (MovieInput) → 201 Movie | 400. */
export function createMovie(input) {
  return mockResponse(MOVIE_SERVICE_URL, "POST", "/movies", () => {
    const movie = { id: nextId++, creationDate: new Date().toISOString().slice(0, 10), genre: null, screenwriter: null, ...input }
    movies = [...movies, movie]
    return movie
  })
}

/** PATCH /movies/{id} (MoviePatch: только изменённые поля) → Movie | 400 | 404. */
export function patchMovie(id, patch) {
  return mockResponse(MOVIE_SERVICE_URL, "PATCH", `/movies/${id}`, () => {
    const movie = { ...findOrThrow(id), ...patch }
    movies = movies.map((m) => (m.id === id ? movie : m))
    return movie
  })
}

/** DELETE /movies/{id} → 204 | 404. */
export function deleteMovie(id) {
  return mockResponse(MOVIE_SERVICE_URL, "DELETE", `/movies/${id}`, () => {
    findOrThrow(id)
    movies = movies.filter((m) => m.id !== id)
  })
}

/** DELETE /movies/by-genre/{genre} → DeletionResult. Фильмы без жанра не удаляются. */
export function deleteMoviesByGenre(genre) {
  return mockResponse(MOVIE_SERVICE_URL, "DELETE", `/movies/by-genre/${genre}`, () => {
    const before = movies.length
    movies = movies.filter((m) => m.genre !== genre)
    return { deletedCount: before - movies.length }
  })
}

/**
 * GET /movies/{genres|mpaa-ratings|colors|countries}?search → { items }.
 * Поиск на стороне сервиса: contains без учёта регистра.
 */
export function searchDictionary(dictionary, search) {
  const query = search ? `?search=${encodeURIComponent(search)}` : ""
  return mockResponse(MOVIE_SERVICE_URL, "GET", `/movies/${dictionary}${query}`, () => ({
    items: enumValues(dictionary).filter((v) => v.toLowerCase().includes((search ?? "").toLowerCase())),
  }))
}

function findOrThrow(id) {
  const movie = movies.find((m) => m.id === id)
  if (!movie) {
    throw new ApiError(404, { message: `Фильм с id=${id} не найден`, timestamp: new Date().toISOString() })
  }
  return movie
}
