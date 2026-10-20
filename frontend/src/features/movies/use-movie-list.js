import { useCallback, useEffect, useState } from "react"

import { listMovies } from "@/api/movie-service"
import { useStoredState } from "@/hooks/use-stored-state"
import { ApiError, notifyError, queryErrors } from "@/lib/api-errors"
import { DICTIONARIES } from "@/lib/enums"
import { buildMovieListParams, DEFAULT_PAGE_SIZE, field, operatorsFor, PAGE_SIZES, requiresValue } from "@/lib/movie-query"

const EMPTY_PAGE = { items: [], page: 0, size: DEFAULT_PAGE_SIZE, totalElements: 0, totalPages: 0 }

const INITIAL_QUERY = { filters: [], sorts: [], page: 0, size: DEFAULT_PAGE_SIZE }

const VALUE_FORMATS = {
  integer: /^-?\d+$/,
  long: /^-?\d+$/,
  date: /^\d{4}-\d{2}-\d{2}$/,
}

function validFilter(f) {
  const meta = f && field(f.field)
  if (!meta || !operatorsFor(f.field).includes(f.operator)) return false
  if (!requiresValue(f.operator)) return f.value === undefined
  if (typeof f.value !== "string" || f.value.trim() === "") return false
  if (meta.type === "enum") return f.value in DICTIONARIES[meta.dictionary].labels
  return VALUE_FORMATS[meta.type]?.test(f.value) ?? true
}

/** Сохранённое состояние из localStorage: то, что больше не подходит под спеку, отбрасывается. */
function sanitizeQuery(stored) {
  const sorts = Array.isArray(stored?.sorts)
    ? stored.sorts.filter((s, i, all) => field(s?.field)?.sortable && ["asc", "desc"].includes(s.direction)
      && all.findIndex((o) => o.field === s.field) === i)
    : []
  return {
    filters: Array.isArray(stored?.filters) ? stored.filters.filter(validFilter) : [],
    sorts,
    page: Number.isInteger(stored?.page) && stored.page >= 0 ? stored.page : 0,
    size: PAGE_SIZES.includes(stored?.size) ? stored.size : DEFAULT_PAGE_SIZE,
  }
}

/**
 * Состояние таблицы фильмов: filter, sort, page, size → GET /movies.
 * Смена фильтров или размера страницы возвращает на первую страницу.
 * filter, sort, page и size хранятся в localStorage — переживают переход на другую страницу и перезагрузку.
 */
export function useMovieList() {
  const [query, setQuery] = useStoredState("movies:query", INITIAL_QUERY, sanitizeQuery)
  const { filters, sorts, page, size } = query
  const [data, setData] = useState(EMPTY_PAGE)
  const [loading, setLoading] = useState(true)
  // ошибки 400 по параметрам: { "filter[1]": "...", "sort[0]": "...", "size": "..." }
  const [paramErrors, setParamErrors] = useState({})
  const [reloadKey, setReloadKey] = useState(0)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    listMovies({ filters, sorts, page, size })
      .then((result) => {
        if (cancelled) return
        setData(result)
        setParamErrors({})
        // сохранённой страницы больше нет (фильмов стало меньше) — переходим на последнюю
        if (result.totalPages > 0 && page >= result.totalPages) {
          setQuery((q) => ({ ...q, page: result.totalPages - 1 }))
        }
      })
      .catch((error) => {
        if (cancelled) return
        if (error instanceof ApiError && error.status === 400) {
          setParamErrors(queryErrors(error))
        }
        notifyError(error)
      })
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [filters, sorts, page, size, reloadKey, setQuery])

  const setFilters = useCallback((next) => setQuery((q) => ({ ...q, filters: next, page: 0 })), [setQuery])
  const setSorts = useCallback((next) => setQuery((q) => ({ ...q, sorts: next })), [setQuery])
  const setPage = useCallback((next) => setQuery((q) => ({ ...q, page: next })), [setQuery])
  const setSize = useCallback((next) => setQuery((q) => ({ ...q, size: next, page: 0 })), [setQuery])

  const reload = useCallback(() => setReloadKey((k) => k + 1), [])

  return {
    filters, setFilters,
    sorts, setSorts,
    page, setPage,
    size, setSize,
    data, loading, paramErrors, reload,
    requestQuery: buildMovieListParams({ filters, sorts, page, size }).toString(),
  }
}
