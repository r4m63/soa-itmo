package com.itmo.soa.movie.api.http;

import com.itmo.soa.contract.error.FieldError;
import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.ValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Параметры пути принимаются строками: на неверное значение — 400 с описанием, а не 404 от JAX-RS.
 */
public final class PathParams {

    private static final String MESSAGE = "Некорректные параметры запроса";

    private PathParams() {
    }

    public static int movieId(String raw) {
        try {
            int id = Integer.parseInt(raw);
            if (id >= 1) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // ниже — общее сообщение
        }
        throw invalid("id", raw, "id должен быть положительным целым числом");
    }

    public static MovieGenre genre(String raw) {
        return Arrays.stream(MovieGenre.values())
                .filter(genre -> genre.name().equals(raw))
                .findFirst()
                .orElseThrow(() -> invalid("genre", raw, "Допустимые значения: " + Arrays.stream(MovieGenre.values())
                        .map(Enum::name).collect(Collectors.joining(", "))));
    }

    private static ValidationException invalid(String field, String raw, String message) {
        return new ValidationException(MESSAGE, List.of(FieldError.path(field, raw, message)));
    }
}
