// Значения enum из спецификаций и подписи для интерфейса.

export const DICTIONARIES = {
  genres: {
    title: "Жанр",
    labels: {
      ACTION: "Боевик",
      DRAMA: "Драма",
      COMEDY: "Комедия",
      MUSICAL: "Мюзикл",
      HORROR: "Ужасы",
    },
  },
  "mpaa-ratings": {
    title: "Рейтинг MPAA",
    labels: {
      G: "G",
      PG: "PG",
      PG_13: "PG-13",
      R: "R",
      NC_17: "NC-17",
    },
  },
  colors: {
    title: "Цвет волос",
    labels: {
      RED: "Красный",
      BLUE: "Синий",
      ORANGE: "Рыжий",
      WHITE: "Белый",
    },
  },
  countries: {
    title: "Национальность",
    labels: {
      RUSSIA: "Россия",
      UNITED_KINGDOM: "Великобритания",
      FRANCE: "Франция",
      SPAIN: "Испания",
      NORTH_KOREA: "КНДР",
    },
  },
}

/** Значения в порядке объявления — этот порядок используют gt/lt и сортировка. */
export function enumValues(dictionary) {
  return Object.keys(DICTIONARIES[dictionary].labels)
}

export function enumLabel(dictionary, value) {
  if (value == null) return null
  return DICTIONARIES[dictionary].labels[value] ?? value
}
