package com.itmo.soa.movie.domain.movie.mapper;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.model.Coordinates;
import com.itmo.soa.movie.domain.movie.model.Country;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MovieName;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.itmo.soa.movie.domain.movie.model.OscarsCount;
import com.itmo.soa.movie.domain.movie.model.Person;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Запросы контракта → доменная модель. Здесь семантика PATCH (null — поле не передано,
 * Optional.empty() — сбросить) и перевод строк справочников в enum: неизвестные значения — все ошибки сразу.
 */
public final class MovieRequestMapper {

    private static final String INVALID_BODY = "Некорректное тело запроса";
    private static final String INVALID_PARAMS = "Некорректные параметры запроса";

    private MovieRequestMapper() {
    }

    public static Movie toNewMovie(CreateMovieRequest request, Clock clock) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre genre = parse(MovieGenre.class, request.getGenre(), FieldError.BODY, "genre", errors);
        MpaaRating mpaaRating = parse(MpaaRating.class, request.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person screenwriter = toPerson(request.getScreenwriter(), errors);
        throwIfAny(INVALID_BODY, errors);
        return Movie.create(new MovieName(request.getName()), toCoordinates(request.getCoordinates()),
                new OscarsCount(request.getOscarsCount()), genre, mpaaRating, screenwriter, clock);
    }

    /** Изменяет только переданные поля; при неизвестных значениях справочников фильм не меняется. */
    public static void applyPatch(Movie movie, UpdateMovieRequest patch) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre genre = patch.getGenre() == null ? null
                : parse(MovieGenre.class, patch.getGenre().orElse(null), FieldError.BODY, "genre", errors);
        MpaaRating mpaaRating = parse(MpaaRating.class, patch.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person screenwriter = patch.getScreenwriter() == null ? null
                : toPerson(patch.getScreenwriter().orElse(null), errors);
        throwIfAny(INVALID_BODY, errors);

        if (patch.getName() != null) {
            movie.rename(new MovieName(patch.getName()));
        }
        if (patch.getCoordinates() != null) {
            movie.moveTo(toCoordinates(patch.getCoordinates()));
        }
        if (patch.getOscarsCount() != null) {
            movie.changeOscars(new OscarsCount(patch.getOscarsCount()));
        }
        if (patch.getGenre() != null) {
            movie.changeGenre(genre);
        }
        if (mpaaRating != null) {
            movie.rate(mpaaRating);
        }
        if (patch.getScreenwriter() != null) {
            if (screenwriter == null) {
                movie.removeScreenwriter();
            } else {
                movie.assignScreenwriter(screenwriter);
            }
        }
    }

    /** Жанр из параметра пути DELETE /movies/by-genre/{genre}. */
    public static MovieGenre genreFromPath(String genre) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre value = parse(MovieGenre.class, genre, FieldError.PATH, "genre", errors);
        throwIfAny(INVALID_PARAMS, errors);
        return value;
    }

    private static Coordinates toCoordinates(CoordinatesDto dto) {
        return new Coordinates(dto.getX(), dto.getY());
    }

    private static Person toPerson(PersonDto dto, List<FieldError> errors) {
        if (dto == null) {
            return null;
        }
        Color hairColor = parse(Color.class, dto.getHairColor(), FieldError.BODY, "screenwriter.hairColor", errors);
        Country nationality = parse(Country.class, dto.getNationality(), FieldError.BODY, "screenwriter.nationality", errors);
        return errors.isEmpty() ? new Person(dto.getName(), dto.getPassportID(), hairColor, nationality) : null;
    }

    /** null — значения нет. Неизвестное имя — ошибка в errors и null. */
    private static <E extends Enum<E>> E parse(Class<E> type, String value, String location, String field,
                                               List<FieldError> errors) {
        if (value == null) {
            return null;
        }
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equals(value)) {
                return constant;
            }
        }
        errors.add(new FieldError(location, field, value, "Допустимые значения: " + Arrays.stream(type.getEnumConstants())
                .map(Enum::name).collect(Collectors.joining(", "))));
        return null;
    }

    private static void throwIfAny(String message, List<FieldError> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidDataException(message, errors);
        }
    }
}
