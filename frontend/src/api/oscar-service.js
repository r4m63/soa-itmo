
import { OSCAR_SERVICE_URL } from "./config"
import { request } from "./http"

export function rewardRRatedMovies() {
  return request(OSCAR_SERVICE_URL, "POST", "/oscar/movies/reward-r")
}

export function redistributeOscars(fromGenre, toGenre) {
  const path = `/oscar/genres/redistribute-rewards/${encodeURIComponent(fromGenre)}/${encodeURIComponent(toGenre)}`
  return request(OSCAR_SERVICE_URL, "POST", path)
}
