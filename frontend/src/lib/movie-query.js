import { enumLabel } from "./enums"
import { formatDate } from "./format"

// Модель filter/sort/page/size для GET /movies — как в таблицах спецификации.

/** Поля коллекции: тип, nullable, можно ли сортировать, справочник для enum. */
export const MOVIE_FIELDS = [
  { key: "id", label: "ID", type: "integer", sortable: true },
  { key: "name", label: "Название", type: "string", sortable: true },
  { key: "coordinates.x", label: "Координата X", type: "integer", sortable: true },
  { key: "coordinates.y", label: "Координата Y", type: "long", sortable: true },
  { key: "creationDate", label: "Дата создания", type: "date", sortable: true },
  { key: "oscarsCount", label: "Оскары", type: "integer", sortable: true },
  { key: "genre", label: "Жанр", type: "enum", dictionary: "genres", nullable: true, sortable: true },
  { key: "mpaaRating", label: "Рейтинг MPAA", type: "enum", dictionary: "mpaa-ratings", sortable: true },
  { key: "screenwriter", label: "Сценарист", type: "object", nullable: true, sortable: false },
  { key: "screenwriter.name", label: "Имя сценариста", type: "string", nullable: true, sortable: true },
  { key: "screenwriter.passportID", label: "Паспорт сценариста", type: "string", nullable: true, sortable: true },
  { key: "screenwriter.hairColor", label: "Цвет волос", type: "enum", dictionary: "colors", nullable: true, sortable: true },
  { key: "screenwriter.nationality", label: "Национальность", type: "enum", dictionary: "countries", nullable: true, sortable: true },
]

/** Названия операторов словами: подписи в списках и текст для скринридера. Иконки — components/operator-icon. */
export const OPERATORS = {
  eq: "равно",
  ne: "не равно",
  gt: "больше",
  gte: "больше или равно",
  lt: "меньше",
  lte: "меньше или равно",
  contains: "содержит",
  startsWith: "начинается с",
  endsWith: "заканчивается на",
  isNull: "пусто",
  notNull: "не пусто",
}

const COMPARISON = ["eq", "ne", "gt", "gte", "lt", "lte"]
const OPERATORS_BY_TYPE = {
  integer: COMPARISON,
  long: COMPARISON,
  date: COMPARISON,
  enum: COMPARISON,
  string: [...COMPARISON, "contains", "startsWith", "endsWith"],
  object: [],
}

export const PAGE_SIZES = [10, 20, 50, 100]
export const DEFAULT_PAGE_SIZE = 20

export function field(key) {
  return MOVIE_FIELDS.find((f) => f.key === key)
}

export function operatorsFor(key) {
  const f = field(key)
  const ops = OPERATORS_BY_TYPE[f.type]
  return f.nullable ? [...ops, "isNull", "notNull"] : ops
}

export function requiresValue(operator) {
  return operator !== "isNull" && operator !== "notNull"
}

/** { field, operator, value } → "field;operator;value" или "field;operator". */
export function filterToParam({ field, operator, value }) {
  return requiresValue(operator) ? `${field};${operator};${value ?? ""}` : `${field};${operator}`
}

/** { field, direction } → "field;asc". */
export function sortToParam({ field, direction }) {
  return `${field};${direction}`
}

/** Состояние таблицы → query-параметры GET /movies. page — с 0, как в API. */
export function buildMovieListParams({ filters, sorts, page, size }) {
  const params = new URLSearchParams()
  filters.forEach((f) => params.append("filter", filterToParam(f)))
  sorts.forEach((s) => params.append("sort", sortToParam(s)))
  params.set("page", String(page))
  params.set("size", String(size))
  return params
}

/** Фильтры одного поля с их позициями в общем списке (позиция нужна для ошибок filter[i]). */
export function fieldFilters(filters, key) {
  return filters.map((f, index) => ({ ...f, index })).filter((f) => f.field === key)
}

/** Заменяет фильтры поля новыми; фильтры других полей и их порядок не меняются. */
export function replaceFieldFilters(filters, key, next) {
  return [...filters.filter((f) => f.field !== key), ...next.map((f) => ({ ...f, field: key }))]
}

/** Клик по заголовку: нет → asc → desc → нет. Новое поле добавляется в конец (меньший приоритет). */
export function toggleSort(sorts, key) {
  const current = sorts.find((s) => s.field === key)
  if (!current) return [...sorts, { field: key, direction: "asc" }]
  if (current.direction === "asc") {
    return sorts.map((s) => (s.field === key ? { ...s, direction: "desc" } : s))
  }
  return sorts.filter((s) => s.field !== key)
}

/** Значение фильтра так, как его видит пользователь: подпись enum, дата по-русски. */
export function filterValueLabel(filter) {
  const f = field(filter.field)
  if (f.type === "enum") return enumLabel(f.dictionary, filter.value)
  if (f.type === "date") return formatDate(filter.value)
  return filter.value
}

/** Фильтр словами — для aria-label и подсказок: «Оскары больше или равно 2», «Жанр: пусто». */
export function describeFilter(filter) {
  const f = field(filter.field)
  const op = OPERATORS[filter.operator]
  return requiresValue(filter.operator) ? `${f.label} ${op} ${filterValueLabel(filter)}` : `${f.label}: ${op}`
}
