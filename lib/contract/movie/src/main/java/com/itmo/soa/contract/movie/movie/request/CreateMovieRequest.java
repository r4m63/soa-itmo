package com.itmo.soa.contract.movie.movie.request;

import com.itmo.soa.contract.movie.movie.dto.CoordinatesDto;
import com.itmo.soa.contract.movie.movie.dto.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /movies. id и creationDate назначает сервис.
 * Значения справочников (genre, mpaaRating) — строки; допустимые значения проверяет сервис.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {

    @NotBlank(message = "Название обязательно и не может быть пустым")
    private String name;

    @NotNull(message = "Поле обязательно")
    @Valid
    private CoordinatesDto coordinates;

    @NotNull(message = "Поле обязательно")
    @Positive(message = "Значение должно быть больше 0")
    private Integer oscarsCount;

    private String genre;

    @NotNull(message = "Поле обязательно")
    private String mpaaRating;

    @Valid
    private PersonDto screenwriter;
}
