package com.itmo.soa.contract.request.movie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {

    @NotNull(message = "Поле обязательно")
    @Pattern(regexp = ".*\\S.*", message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Поле обязательно")
    @Valid
    private CoordinatesDto coordinates;

    @NotNull(message = "Поле обязательно")
    @Positive(message = "Значение должно быть больше 0")
    private Integer oscarsCount;

    @Pattern(
            regexp = "ACTION|DRAMA|COMEDY|MUSICAL|HORROR",
            message = "Допустимые значения: ACTION, DRAMA, COMEDY, MUSICAL, HORROR"
    )
    private String genre;

    @NotNull(message = "Поле обязательно")
    @Pattern(
            regexp = "G|PG|PG_13|R|NC_17",
            message = "Допустимые значения: G, PG, PG_13, R, NC_17"
    )
    private String mpaaRating;

    @Valid
    private PersonDto screenwriter;
}
