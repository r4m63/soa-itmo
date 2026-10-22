import { toast } from "sonner"

/**
 * Ошибка ответа сервиса. body — тело в формате спецификации:
 * Error { message, timestamp } или ValidationError { message, timestamp, errors[] }.
 */
export class ApiError extends Error {
  constructor(status, body) {
    super(body?.message ?? `Ошибка ${status}`)
    this.status = status
    this.body = body
  }

  get errors() {
    return this.body?.errors ?? []
  }
}

/** errors[] с location body → { "coordinates.x": "сообщение", ... } для подсветки полей формы. */
export function bodyFieldErrors(error) {
  return Object.fromEntries(
    errorsAt(error, "body").filter((e) => e.field).map((e) => [e.field, e.message])
  )
}

/** Ошибки всего тела (без field): невалидный JSON, пустой PATCH. */
export function bodyGeneralErrors(error) {
  return errorsAt(error, "body").filter((e) => !e.field).map((e) => e.message)
}

/** errors[] с location query → { "filter[1]": "...", "sort[0]": "...", "size": "..." }. */
export function queryErrors(error) {
  return Object.fromEntries(errorsAt(error, "query").map((e) => [e.field, e.message]))
}

/** errors[] с location path → { "to-genre": "..." }. */
export function pathErrors(error) {
  return Object.fromEntries(errorsAt(error, "path").map((e) => [e.field, e.message]))
}

function errorsAt(error, location) {
  return error instanceof ApiError ? error.errors.filter((e) => e.location === location) : []
}

const TITLES = {
  400: "Сервис отклонил данные",
  404: "Не найдено",
  409: "Операция невозможна",
  503: "Сервис недоступен",
}

/**
 * Всплывающее сообщение об ошибке сервиса — единственное место, где ошибка показывается текстом
 * (у полей остаётся только подсветка). details — ошибки, которых нет у полей формы или таблицы.
 * 4xx — warning: запрос можно исправить (данные, фильтры, выбор жанров); 5xx и обрыв связи — error.
 */
export function notifyError(error, details = []) {
  if (error instanceof ApiError) {
    const notify = error.status < 500 ? toast.warning : toast.error
    notify(TITLES[error.status] ?? "Ошибка сервиса", { description: [error.message, ...details].join(". ") })
  } else {
    toast.error("Не удалось выполнить запрос", { description: String(error?.message ?? error) })
  }
}
