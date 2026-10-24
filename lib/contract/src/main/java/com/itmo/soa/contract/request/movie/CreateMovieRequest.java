package com.itmo.soa.contract.request.movie;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {

    @Null(message = "Поле id назначает сервис")
    private Object id;

    @Null(message = "Поле creationDate назначает сервис")
    private Object creationDate;

    @NotNull(message = "Поле обязательно")
    @Pattern(regexp = ".*\\S.*", message = "Название не может быть пустым")
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
