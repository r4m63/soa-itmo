package com.itmo.soa.contract.movie;

public record MovieInput(
        String name,
        Coordinates coordinates,
        Integer oscarsCount,
        MovieGenre genre,
        MpaaRating mpaaRating,
        Person screenwriter
) {
}
