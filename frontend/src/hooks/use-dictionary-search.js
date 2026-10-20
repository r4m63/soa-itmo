import { useEffect, useState } from "react"

import { searchDictionary } from "@/api/movie-service"
import { notifyError } from "@/lib/api-errors"
import { enumLabel, enumValues } from "@/lib/enums"

const SEARCH_DELAY_MS = 250

/**
 * Значения справочника по поиску: GET /movies/{dictionary}?search= (сервис ищет по кодам: DRAMA)
 * плюс совпадения по подписям интерфейса («драма»), которых сервис не знает. Порядок — как в enum.
 */
export function useDictionarySearch(dictionary, search, enabled) {
  const [items, setItems] = useState(() => enumValues(dictionary))
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!enabled) return
    let cancelled = false
    const timer = setTimeout(async () => {
      setLoading(true)
      try {
        const found = new Set((await searchDictionary(dictionary, search)).items)
        const query = search.toLowerCase()
        if (!cancelled) {
          setItems(enumValues(dictionary).filter((v) => found.has(v) || enumLabel(dictionary, v).toLowerCase().includes(query)))
        }
      } catch (error) {
        notifyError(error)
      } finally {
        if (!cancelled) setLoading(false)
      }
    }, SEARCH_DELAY_MS)
    return () => {
      cancelled = true
      clearTimeout(timer)
    }
  }, [dictionary, search, enabled])

  return { items, loading }
}
