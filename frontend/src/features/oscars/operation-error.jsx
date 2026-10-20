import { CircleAlertIcon } from "lucide-react"

import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { ApiError } from "@/lib/api-errors"

const TITLES = {
  400: "Неверные параметры",
  409: "Операция невозможна",
  503: "Movie Service недоступен",
}

/** Ошибка последней попытки операции — остаётся на карточке, пока её не повторят. */
export function OperationError({ error }) {
  if (!error) return null
  const status = error instanceof ApiError ? error.status : null
  return (
    <Alert variant="destructive">
      <CircleAlertIcon />
      <AlertTitle>{TITLES[status] ?? "Не удалось выполнить операцию"}</AlertTitle>
      <AlertDescription>{error.message}</AlertDescription>
    </Alert>
  )
}
