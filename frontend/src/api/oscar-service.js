// Oscar Service (Spring MVC, Payara №2). Функции повторяют операции docs/oscar-service.yaml.

import { OSCAR_SERVICE_URL } from "./config"
import { request } from "./http"

/** POST /oscar/movies/reward-r → BulkOperationResult | 503. */
export function rewardRRatedMovies() {
  return request(OSCAR_SERVICE_URL, "POST", "/oscar/movies/reward-r")
}

/** POST /oscar/genres/redistribute-rewards/{from}/{to} → RedistributionResult | 400 | 409 | 503. */
export function redistributeOscars(fromGenre, toGenre) {
  const path = `/oscar/genres/redistribute-rewards/${encodeURIComponent(fromGenre)}/${encodeURIComponent(toGenre)}`
  return request(OSCAR_SERVICE_URL, "POST", path)
}
