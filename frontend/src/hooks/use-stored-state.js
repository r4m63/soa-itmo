import { useEffect, useState } from "react"

const PREFIX = "kinoteka:v1:"

function read(key, sanitize, fallback) {
  try {
    const raw = localStorage.getItem(PREFIX + key)
    return raw === null ? fallback : sanitize(JSON.parse(raw))
  } catch {
    // хранилище недоступно или в нём мусор — начинаем с начального значения
    return fallback
  }
}

/**
 * useState, сохраняемый в localStorage: переживает переходы между страницами и перезагрузку,
 * изменения из других вкладок подхватываются через событие storage.
 * sanitize проверяет прочитанное значение (оно могло устареть вместе с кодом) и возвращает допустимое.
 */
export function useStoredState(key, initial, sanitize = (v) => v) {
  const [value, setValue] = useState(() => read(key, sanitize, initial))

  useEffect(() => {
    try {
      localStorage.setItem(PREFIX + key, JSON.stringify(value))
    } catch {
      // без сохранения всё продолжает работать
    }
  }, [key, value])

  useEffect(() => {
    const onStorage = (e) => {
      // key === null — хранилище очистили целиком
      if (e.key === PREFIX + key || e.key === null) setValue(read(key, sanitize, initial))
    }
    window.addEventListener("storage", onStorage)
    return () => window.removeEventListener("storage", onStorage)
    // sanitize и initial — константы вызывающего кода, подписка зависит только от ключа
  }, [key])

  return [value, setValue]
}
