CREATE TABLE movies
(
    id                       SERIAL PRIMARY KEY,
    name                     TEXT    NOT NULL CHECK (name <> ''),
    coordinates_x            INTEGER NOT NULL,
    coordinates_y            BIGINT  NOT NULL,
    creation_date            DATE    NOT NULL,
    oscars_count             INTEGER NOT NULL CHECK (oscars_count > 0),
    genre                    VARCHAR(16),
    mpaa_rating              VARCHAR(16) NOT NULL,
    screenwriter_name        TEXT,
    screenwriter_passport_id TEXT CHECK (char_length(screenwriter_passport_id) >= 5),
    screenwriter_hair_color  VARCHAR(16),
    screenwriter_nationality VARCHAR(32),
    -- сценарист либо целиком отсутствует, либо у него есть имя и национальность
    CHECK ((screenwriter_name IS NULL AND screenwriter_passport_id IS NULL
                AND screenwriter_hair_color IS NULL AND screenwriter_nationality IS NULL)
        OR (screenwriter_name IS NOT NULL AND screenwriter_nationality IS NOT NULL))
);
