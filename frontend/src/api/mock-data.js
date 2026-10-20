// Демо-данные в формате схемы Movie из спецификации.

const NAMES = [
  "Film name 1", "Burnt by the Sun", "Moscow Does Not Believe in Tears", "The Artist", "Amélie",
  "The King's Speech", "Slumdog Millionaire", "Les Misérables", "Gladiator", "Chicago",
  "The Shining", "Alien", "La La Land", "Whiplash", "Parasite",
  "Oldboy", "Pan's Labyrinth", "The Others", "Volver", "Stalker",
  "Solaris", "Brother", "Leviathan", "The Return", "Amadeus",
  "Cabaret", "Grease", "Hereditary", "The Witch", "Mad Max: Fury Road",
  "Heat", "Ronin", "Notting Hill", "Love Actually", "Paddington",
  "Trainspotting", "Snatch",
]
const GENRES = ["ACTION", "DRAMA", "COMEDY", "MUSICAL", "HORROR", null]
const RATINGS = ["G", "PG", "PG_13", "R", "NC_17"]
const COLORS = ["RED", "BLUE", "ORANGE", "WHITE", null]
const COUNTRIES = ["RUSSIA", "UNITED_KINGDOM", "FRANCE", "SPAIN", "NORTH_KOREA"]

export const mockMovies = NAMES.map((name, i) => ({
  id: i + 1,
  name,
  coordinates: { x: (i * 37) % 200 - 100, y: (i * 911) % 5000 - 2500 },
  creationDate: `2026-09-${String((i % 28) + 1).padStart(2, "0")}`,
  oscarsCount: (i % 7) + 1,
  genre: GENRES[i % GENRES.length],
  mpaaRating: RATINGS[i % RATINGS.length],
  screenwriter: i % 4 === 3
    ? null
    : {
        name: `Author ${i + 1}`,
        passportID: i % 3 === 0 ? null : `AB${100000 + i * 17}`,
        hairColor: COLORS[i % COLORS.length],
        nationality: COUNTRIES[i % COUNTRIES.length],
      },
}))
