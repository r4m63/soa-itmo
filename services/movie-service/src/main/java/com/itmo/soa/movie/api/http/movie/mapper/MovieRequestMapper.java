package com.itmo.soa.movie.api.http.movie.mapper;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.movie.domain.movie.service.CreateMovieCommand;
import com.itmo.soa.movie.domain.movie.service.PatchMovieCommand;
import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.model.Coordinates;
import com.itmo.soa.movie.domain.movie.model.Country;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MovieName;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.itmo.soa.movie.domain.movie.model.OscarsCount;
import com.itmo.soa.movie.domain.movie.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MovieRequestMapper {

    private static final String INVALID_BODY = "Некорректное тело запроса";
    private static final String INVALID_PARAMS = "Некорректные параметры запроса";

    private MovieRequestMapper() {
    }

    public static CreateMovieCommand toCommand(CreateMovieRequest request) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre genre = parse(MovieGenre.class, request.getGenre(), FieldError.BODY, "genre", errors);
        MpaaRating mpaaRating = parse(MpaaRating.class, request.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person screenwriter = toPerson(request.getScreenwriter(), errors);
        throwIfAny(INVALID_BODY, errors);
        return new CreateMovieCommand(new MovieName(request.getName()), toCoordinates(request.getCoordinates()),
                new OscarsCount(request.getOscarsCount()), genre, mpaaRating, screenwriter);
    }

    public static PatchMovieCommand toCommand(UpdateMovieRequest patch) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre genre = patch.getGenre() == null ? null
                : parse(MovieGenre.class, patch.getGenre().orElse(null), FieldError.BODY, "genre", errors);
        MpaaRating mpaaRating = parse(MpaaRating.class, patch.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person screenwriter = patch.getScreenwriter() == null ? null
                : toPerson(patch.getScreenwriter().orElse(null), errors);
        throwIfAny(INVALID_BODY, errors);

        MovieName name = patch.getName() == null ? null : new MovieName(patch.getName());
        Coordinates coordinates = patch.getCoordinates() == null ? null : toCoordinates(patch.getCoordinates());
        OscarsCount oscarsCount = patch.getOscarsCount() == null ? null : new OscarsCount(patch.getOscarsCount());
        Optional<MovieGenre> genreChange = patch.getGenre() == null ? null : Optional.ofNullable(genre);
        Optional<Person> screenwriterChange = patch.getScreenwriter() == null ? null : Optional.ofNullable(screenwriter);
        return new PatchMovieCommand(name, coordinates, oscarsCount, genreChange, mpaaRating, screenwriterChange);
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
