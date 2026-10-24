import { useEffect, useState } from "react"

import { searchDictionary } from "@/api/movie-service"
import { notifyError } from "@/lib/api-errors"
import { enumLabel, enumValues } from "@/lib/enums"

const loaded = new Map()

function loadDictionary(dictionary) {
  if (!loaded.has(dictionary)) {
    const request = searchDictionary(dictionary).then(({ items }) => items)
    request.catch(() => loaded.delete(dictionary))
    loaded.set(dictionary, request)
  }
  return loaded.get(dictionary)
}

export function useDictionarySearch(dictionary, search, enabled) {
  const [values, setValues] = useState(() => enumValues(dictionary))
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!enabled) return
    let cancelled = false
    setLoading(true)
    loadDictionary(dictionary)
      .then((items) => !cancelled && setValues(items))
      .catch((error) => !cancelled && notifyError(error))
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [dictionary, enabled])

  const query = search.trim().toLowerCase()
  const items = query
    ? values.filter((v) => v.toLowerCase().includes(query) || enumLabel(dictionary, v).toLowerCase().includes(query))
    : values
  return { items, loading }
}
