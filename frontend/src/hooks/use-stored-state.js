import { useEffect, useState } from "react"

const PREFIX = "kinoteka:v1:"

function read(key, sanitize, fallback) {
  try {
    const raw = localStorage.getItem(PREFIX + key)
    return raw === null ? fallback : sanitize(JSON.parse(raw))
  } catch {
    return fallback
  }
}

export function useStoredState(key, initial, sanitize = (v) => v) {
  const [value, setValue] = useState(() => read(key, sanitize, initial))

  useEffect(() => {
    try {
      localStorage.setItem(PREFIX + key, JSON.stringify(value))
    } catch {
    }
  }, [key, value])

  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === PREFIX + key || e.key === null) setValue(read(key, sanitize, initial))
    }
    window.addEventListener("storage", onStorage)
    return () => window.removeEventListener("storage", onStorage)
  }, [key])

  return [value, setValue]
}
