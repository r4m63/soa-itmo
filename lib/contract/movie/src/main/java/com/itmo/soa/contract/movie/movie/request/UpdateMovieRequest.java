package com.itmo.soa.contract.movie.movie.request;

import com.itmo.soa.contract.movie.movie.dto.CoordinatesDto;
import com.itmo.soa.contract.movie.movie.dto.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * PATCH /movies/{id}. null — поле не передано, его значение не меняется.
 * genre и screenwriter можно сбросить: Optional.empty() — передан null, Optional.of(...) — новое значение.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequest {

    @Pattern(regexp = ".*\\S.*", message = "Название не может быть пустым")
    private String name;

    @Valid
    private CoordinatesDto coordinates;

    @Positive(message = "Значение должно быть больше 0")
    private Integer oscarsCount;

    private Optional<String> genre;

    private String mpaaRating;

    private Optional<@Valid PersonDto> screenwriter;

    @AssertTrue(message = "Нужно передать хотя бы одно поле")
    public boolean isNotEmpty() {
        return Stream.of(name, coordinates, oscarsCount, genre, mpaaRating, screenwriter).anyMatch(v -> v != null);
    }
}
