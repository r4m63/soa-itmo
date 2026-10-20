// Oscar Service (Spring MVC, Payara №2). Функции повторяют операции docs/oscar-service.yaml.

import { ApiError } from "@/lib/api-errors"
import { OSCAR_SERVICE_URL } from "./config"
import { mockResponse } from "./http"

/** POST /oscar/movies/reward-r → BulkOperationResult | 503. */
export function rewardRRatedMovies() {
  return mockResponse(OSCAR_SERVICE_URL, "POST", "/oscar/movies/reward-r", () => ({ affectedMovies: 7 }))
}

/** POST /oscar/genres/redistribute-rewards/{from}/{to} → RedistributionResult | 400 | 409 | 503. */
export function redistributeOscars(fromGenre, toGenre) {
  return mockResponse(OSCAR_SERVICE_URL, "POST", `/oscar/genres/redistribute-rewards/${fromGenre}/${toGenre}`, () => {
    // мок повторяет проверки сервиса, чтобы было видно, как интерфейс показывает 400
    if (fromGenre === toGenre) {
      throw new ApiError(400, {
        message: "Некорректные параметры запроса",
        timestamp: new Date().toISOString(),
        errors: [{ location: "path", field: "to-genre", rejectedValue: toGenre, message: "Жанр назначения должен отличаться от исходного" }],
      })
    }
    return { sourceMovies: 3, targetMovies: 2, transferredOscars: 10 }
  })
}
