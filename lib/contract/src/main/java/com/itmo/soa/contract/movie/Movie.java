package com.itmo.soa.contract.movie;

import java.time.LocalDate;

public record Movie(
        Integer id,
        String name,
        Coordinates coordinates,
        LocalDate creationDate,
        Integer oscarsCount,
        MovieGenre genre,
        MpaaRating mpaaRating,
        Person screenwriter
) {
}
