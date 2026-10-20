const dateFormat = new Intl.DateTimeFormat("ru-RU", { day: "numeric", month: "long", year: "numeric" })

/** "2026-09-20" → "20 сентября 2026 г." */
export function formatDate(isoDate) {
  if (!isoDate) return "—"
  const [y, m, d] = isoDate.split("-").map(Number)
  return dateFormat.format(new Date(y, m - 1, d))
}

/** plural(5, ["фильм", "фильма", "фильмов"]) → "фильмов" */
export function plural(n, [one, few, many]) {
  const mod10 = Math.abs(n) % 10
  const mod100 = Math.abs(n) % 100
  if (mod10 === 1 && mod100 !== 11) return one
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return few
  return many
}

export const MOVIES_WORDS = ["фильм", "фильма", "фильмов"]
export const OSCARS_WORDS = ["Оскар", "Оскара", "Оскаров"]

/** Маска ввода даты: оставляет цифры и расставляет косые черты — "05092026" → "05/09/2026". */
export function maskDate(raw) {
  const digits = raw.replace(/\D/g, "").slice(0, 8)
  return [digits.slice(0, 2), digits.slice(2, 4), digits.slice(4, 8)].filter(Boolean).join("/")
}

/** "05/09/2026" → "2026-09-05"; несуществующая дата → null. */
export function parseMaskedDate(text) {
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(text)
  if (!match) return null
  const [, d, m, y] = match.map(Number)
  const date = new Date(y, m - 1, d)
  if (date.getFullYear() !== y || date.getMonth() !== m - 1 || date.getDate() !== d) return null
  return `${match[3]}-${match[2]}-${match[1]}`
}

/** "2026-09-05" → "05/09/2026". */
export function toMaskedDate(isoDate) {
  if (!isoDate) return ""
  const [y, m, d] = isoDate.split("-")
  return `${d}/${m}/${y}`
}
