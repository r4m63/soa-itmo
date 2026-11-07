package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.movie.domain.movie.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class MovieRequestMapper {

    private static final String INVALID_BODY = "Некорректное тело запроса";
    private static final String INVALID_PARAMS = "Некорректные параметры запроса";

    private MovieRequestMapper() {
    }

    public static Movie toMovie(CreateMovieRequest request) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre genre = parse(MovieGenre.class, request.getGenre(), FieldError.BODY, "genre", errors);
        MpaaRating mpaaRating = parse(MpaaRating.class, request.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person screenwriter = toPerson(request.getScreenwriter(), errors);
        throwIfAny(INVALID_BODY, errors);
        return Movie.create(new MovieName(request.getName()), toCoordinates(request.getCoordinates()),
                new OscarsCount(request.getOscarsCount()), genre, mpaaRating, screenwriter);
    }

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
        if (patch.getMpaaRating() != null) {
            movie.rate(mpaaRating);
        }
        if (patch.getScreenwriter() != null) {
            if (patch.getScreenwriter().isEmpty()) {
                movie.removeScreenwriter();
            } else {
                movie.assignScreenwriter(screenwriter);
            }
        }
    }

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
