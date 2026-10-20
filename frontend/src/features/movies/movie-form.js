// Модель формы фильма: значения полей — строки (как в input), ключи ошибок — пути полей из API.

const INT_MIN = -2147483648
const INT_MAX = 2147483647
const REQUIRED = "Поле обязательно"

export function emptyForm() {
  return {
    name: "",
    x: "",
    y: "",
    oscarsCount: "1",
    genre: null,
    mpaaRating: undefined,
    hasScreenwriter: false,
    screenwriter: { name: "", passportID: "", hairColor: null, nationality: undefined },
  }
}

export function formFromMovie(movie) {
  const sw = movie.screenwriter
  return {
    name: movie.name,
    x: String(movie.coordinates.x),
    y: String(movie.coordinates.y),
    oscarsCount: String(movie.oscarsCount),
    genre: movie.genre,
    mpaaRating: movie.mpaaRating,
    hasScreenwriter: sw != null,
    screenwriter: sw
      ? { name: sw.name, passportID: sw.passportID ?? "", hairColor: sw.hairColor, nationality: sw.nationality }
      : emptyForm().screenwriter,
  }
}

/** Те же ограничения, что проверяет Movie Service. → { "coordinates.x": "...", ... } */
export function validateForm(form) {
  const errors = {}
  if (!form.name.trim()) errors.name = "Название не может быть пустым"
  integer(form.x, "coordinates.x", INT_MIN, INT_MAX, errors)
  integer(form.y, "coordinates.y", Number.MIN_SAFE_INTEGER, Number.MAX_SAFE_INTEGER, errors)
  integer(form.oscarsCount, "oscarsCount", 1, INT_MAX, errors, "Значение должно быть больше 0")
  if (!form.mpaaRating) errors.mpaaRating = REQUIRED
  if (form.hasScreenwriter) {
    const sw = form.screenwriter
    if (!sw.name.trim()) errors["screenwriter.name"] = "Имя не может быть пустым"
    if (sw.passportID && sw.passportID.length < 5) errors["screenwriter.passportID"] = "Длина должна быть не меньше 5 символов"
    if (!sw.nationality) errors["screenwriter.nationality"] = REQUIRED
  }
  return errors
}

/** Форма → MovieInput (тело POST /movies). */
export function toMovieInput(form) {
  const sw = form.screenwriter
  return {
    name: form.name.trim(),
    coordinates: { x: Number(form.x), y: Number(form.y) },
    oscarsCount: Number(form.oscarsCount),
    genre: form.genre ?? null,
    mpaaRating: form.mpaaRating,
    screenwriter: form.hasScreenwriter
      ? { name: sw.name.trim(), passportID: sw.passportID || null, hairColor: sw.hairColor ?? null, nationality: sw.nationality }
      : null,
  }
}

/** Только изменённые поля — тело PATCH /movies/{id}. coordinates и screenwriter отправляются целиком. */
export function diffPatch(movie, input) {
  const original = toMovieInput(formFromMovie(movie))
  return Object.fromEntries(
    Object.keys(input)
      .filter((key) => JSON.stringify(input[key]) !== JSON.stringify(original[key]))
      .map((key) => [key, input[key]])
  )
}

function integer(raw, key, min, max, errors, rangeMessage) {
  if (String(raw).trim() === "") {
    errors[key] = REQUIRED
  } else if (!/^-?\d+$/.test(String(raw).trim())) {
    errors[key] = "Ожидается целое число"
  } else if (Number(raw) < min || Number(raw) > max) {
    errors[key] = rangeMessage ?? `Допустимо от ${min} до ${max}`
  }
}
