package com.itmo.soa.contract.movie;

import com.itmo.soa.contract.common.PatchField;

import java.util.stream.Stream;

/**
 * Тело PATCH /movies/{id}. Собирается из JSON вручную: JSON-библиотеки не различают «поле не передано» и null.
 */
public record MoviePatch(
        PatchField<String> name,
        PatchField<Coordinates> coordinates,
        PatchField<Integer> oscarsCount,
        PatchField<MovieGenre> genre,
        PatchField<MpaaRating> mpaaRating,
        PatchField<Person> screenwriter
) {

    public boolean isEmpty() {
        return Stream.of(name, coordinates, oscarsCount, genre, mpaaRating, screenwriter)
                .noneMatch(PatchField::isPresent);
    }
}
