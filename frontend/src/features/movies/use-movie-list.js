import { useCallback, useEffect, useState } from "react"

import { listMovies } from "@/api/movie-service"
import { ApiError, notifyError, queryErrors } from "@/lib/api-errors"
import { buildMovieListParams, DEFAULT_PAGE_SIZE } from "@/lib/movie-query"

const EMPTY_PAGE = { items: [], page: 0, size: DEFAULT_PAGE_SIZE, totalElements: 0, totalPages: 0 }

/**
 * Состояние таблицы фильмов: filter, sort, page, size → GET /movies.
 * Смена фильтров или размера страницы возвращает на первую страницу.
 */
export function useMovieList() {
  const [filters, setFiltersState] = useState([])
  const [sorts, setSortsState] = useState([])
  const [page, setPage] = useState(0)
  const [size, setSizeState] = useState(DEFAULT_PAGE_SIZE)
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
  }, [filters, sorts, page, size, reloadKey])

  const setFilters = useCallback((next) => {
    setFiltersState(next)
    setPage(0)
  }, [])

  const setSorts = useCallback((next) => setSortsState(next), [])

  const setSize = useCallback((next) => {
    setSizeState(next)
    setPage(0)
  }, [])

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
