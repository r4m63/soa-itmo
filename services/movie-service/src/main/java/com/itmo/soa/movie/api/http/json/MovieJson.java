package com.itmo.soa.movie.api.http.json;

import com.itmo.soa.contract.common.PatchField;
import com.itmo.soa.contract.error.FieldError;
import com.itmo.soa.contract.movie.Color;
import com.itmo.soa.contract.movie.Coordinates;
import com.itmo.soa.contract.movie.Country;
import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.contract.movie.MovieInput;
import com.itmo.soa.contract.movie.MoviePatch;
import com.itmo.soa.contract.movie.MpaaRating;
import com.itmo.soa.contract.movie.Person;
import jakarta.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Разбор тел POST и PATCH /movies. Проверяет только форму JSON (типы, незнакомые поля);
 * ограничения модели проверяет агрегат Movie.
 */
final class MovieJson {

    private static final Set<String> MOVIE_FIELDS =
            Set.of("name", "coordinates", "oscarsCount", "genre", "mpaaRating", "screenwriter");
    private static final Set<String> COORDINATES_FIELDS = Set.of("x", "y");
    private static final Set<String> PERSON_FIELDS = Set.of("name", "passportID", "hairColor", "nationality");

    private MovieJson() {
    }

    static MovieInput input(JsonObject json) {
        List<FieldError> errors = new ArrayList<>();
        JsonFields body = new JsonFields(json, "", errors);
        body.rejectUnknown(MOVIE_FIELDS, Map.of(
                "id", "Поле id задаётся сервером",
                "creationDate", "Поле creationDate задаётся сервером"));
        MovieInput input = new MovieInput(
                body.string("name"),
                coordinates(body.object("coordinates")),
                body.integer("oscarsCount"),
                body.enumeration("genre", MovieGenre.class),
                body.enumeration("mpaaRating", MpaaRating.class),
                person(body.object("screenwriter")));
        JsonFields.throwIfAny(errors);
        return input;
    }

    static MoviePatch patch(JsonObject json) {
        List<FieldError> errors = new ArrayList<>();
        if (json.isEmpty()) {
            errors.add(FieldError.body(null, null, "Нужно передать хотя бы одно поле"));
        }
        JsonFields body = new JsonFields(json, "", errors);
        body.rejectUnknown(MOVIE_FIELDS, Map.of(
                "id", "Поле id нельзя изменить",
                "creationDate", "Поле creationDate нельзя изменить"));
        MoviePatch patch = new MoviePatch(
                field(body, "name", body::string),
                field(body, "coordinates", key -> coordinates(body.object(key))),
                field(body, "oscarsCount", body::integer),
                field(body, "genre", key -> body.enumeration(key, MovieGenre.class)),
                field(body, "mpaaRating", key -> body.enumeration(key, MpaaRating.class)),
                field(body, "screenwriter", key -> person(body.object(key))));
        JsonFields.throwIfAny(errors);
        return patch;
    }

    private static <T> PatchField<T> field(JsonFields body, String key, Function<String, T> reader) {
        return body.has(key) ? PatchField.of(reader.apply(key)) : PatchField.absent();
    }

    private static Coordinates coordinates(JsonFields json) {
        if (json == null) {
            return null;
        }
        json.rejectUnknown(COORDINATES_FIELDS, Map.of());
        return new Coordinates(json.integer("x"), json.longNumber("y"));
    }

    private static Person person(JsonFields json) {
        if (json == null) {
            return null;
        }
        json.rejectUnknown(PERSON_FIELDS, Map.of());
        return new Person(
                json.string("name"),
                json.string("passportID"),
                json.enumeration("hairColor", Color.class),
                json.enumeration("nationality", Country.class));
    }
}
