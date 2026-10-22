package com.itmo.soa.contract.request.movie;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequest {

    @Null(message = "Поле id нельзя изменить")
    private Object id;

    @Null(message = "Поле creationDate нельзя изменить")
    private Object creationDate;

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
        return Stream.of(id, creationDate, name, coordinates, oscarsCount, genre, mpaaRating, screenwriter).anyMatch(v -> v != null);
    }
}
