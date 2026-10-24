import { toast } from "sonner"

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

export function bodyFieldErrors(error) {
  return Object.fromEntries(
    errorsAt(error, "body").filter((e) => e.field).map((e) => [e.field, e.message])
  )
}

export function bodyGeneralErrors(error) {
  return errorsAt(error, "body").filter((e) => !e.field).map((e) => e.message)
}

export function queryErrors(error) {
  return Object.fromEntries(errorsAt(error, "query").map((e) => [e.field, e.message]))
}

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

export function notifyError(error, details = []) {
  if (error instanceof ApiError) {
    const notify = error.status < 500 ? toast.warning : toast.error
    notify(TITLES[error.status] ?? "Ошибка сервиса", { description: [error.message, ...details].join(". ") })
  } else {
    toast.error("Не удалось выполнить запрос", { description: String(error?.message ?? error) })
  }
}
