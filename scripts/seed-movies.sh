#!/bin/sh
set -eu

csv_file=${1:-"$(dirname "$0")/movies.csv"}
if [ ! -r "$csv_file" ]; then
    echo "CSV file is not readable: $csv_file" >&2
    exit 1
fi
csv_file=$(cd "$(dirname "$csv_file")" && pwd)/$(basename "$csv_file")
case "$csv_file" in
    *"'"*) echo "CSV path must not contain an apostrophe" >&2; exit 1 ;;
esac

: "${PGHOST:=localhost}"
: "${PGPORT:=5432}"
: "${PGUSER:=movies}"
: "${PGDATABASE:=movies}"
export PGHOST PGPORT PGUSER PGDATABASE

psql -X -v ON_ERROR_STOP=1 --single-transaction <<SQL
CREATE TEMP TABLE movie_seed (
    title text,
    year integer,
    genre text,
    screenwriter text,
    release_date date
) ON COMMIT DROP;
\copy movie_seed FROM '$csv_file' WITH (FORMAT csv, HEADER true)

WITH inserted AS (
    INSERT INTO movies (
        name, coordinates_x, coordinates_y, creation_date,
        oscars_count, genre, mpaa_rating
    )
    SELECT seed.title, 0, 0, seed.release_date,
           1, seed.genre, 'PG'
    FROM movie_seed AS seed
    WHERE NOT EXISTS (
        SELECT 1 FROM movies AS existing
        WHERE existing.name = seed.title
          AND existing.creation_date = seed.release_date
    )
    RETURNING id
)
SELECT count(*) AS inserted_movies FROM inserted;
SQL
