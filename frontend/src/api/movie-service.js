
import { buildMovieListParams } from "@/lib/movie-query"
import { MOVIE_SERVICE_URL } from "./config"
import { request } from "./http"

export function listMovies({ filters, sorts, page, size }) {
  const params = buildMovieListParams({ filters, sorts, page, size })
  return request(MOVIE_SERVICE_URL, "GET", `/movies?${params}`)
}

export function getMovie(id) {
  return request(MOVIE_SERVICE_URL, "GET", `/movies/${id}`)
}

export function createMovie(input) {
  return request(MOVIE_SERVICE_URL, "POST", "/movies", input)
}

export function patchMovie(id, patch) {
  return request(MOVIE_SERVICE_URL, "PATCH", `/movies/${id}`, patch)
}

export function deleteMovie(id) {
  return request(MOVIE_SERVICE_URL, "DELETE", `/movies/${id}`)
}

export function deleteMoviesByGenre(genre) {
  return request(MOVIE_SERVICE_URL, "DELETE", `/movies/by-genre/${encodeURIComponent(genre)}`)
}

export function searchDictionary(dictionary, search) {
  const query = search ? `?search=${encodeURIComponent(search)}` : ""
  return request(MOVIE_SERVICE_URL, "GET", `/movies/${dictionary}${query}`)
}
