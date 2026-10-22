package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.contract.common.error.FieldError;
import com.itmo.soa.contract.movie.movie.request.CreateMovieRequest;
import com.itmo.soa.contract.movie.movie.request.UpdateMovieRequest;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieGenre;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MpaaRating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Агрегат «Фильм». Ограничения полей проверяются на входе (аннотации CreateMovieRequest, UpdateMovieRequest).
 * id и creationDate задаются при создании и больше не меняются.
 */
public final class Movie {

    private static final String INVALID_BODY = "Некорректное тело запроса";

    private final Integer id;
    private final LocalDate creationDate;
    private String name;
    private Coordinates coordinates;
    private int oscarsCount;
    private MovieGenre genre;
    private MpaaRating mpaaRating;
    private Person screenwriter;

    private Movie(Integer id, String name, Coordinates coordinates, LocalDate creationDate, int oscarsCount,
                  MovieGenre genre, MpaaRating mpaaRating, Person screenwriter) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.oscarsCount = oscarsCount;
        this.genre = genre;
        this.mpaaRating = mpaaRating;
        this.screenwriter = screenwriter;
    }

    /** Новый фильм из запроса, уже проверенного по аннотациям CreateMovieRequest. id назначит хранилище. */
    public static Movie create(CreateMovieRequest input, LocalDate creationDate) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre genre = EnumValues.parse(MovieGenre.class, input.getGenre(), FieldError.BODY, "genre", errors);
        MpaaRating mpaaRating = EnumValues.parse(MpaaRating.class, input.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person screenwriter = Person.from(input.getScreenwriter(), errors);
        EnumValues.throwIfAny(INVALID_BODY, errors);
        return new Movie(null, input.getName(), Coordinates.from(input.getCoordinates()), creationDate,
                input.getOscarsCount(), genre, mpaaRating, screenwriter);
    }

    /** Восстановление уже сохранённого фильма. */
    public static Movie restore(Integer id, String name, Coordinates coordinates, LocalDate creationDate,
                                int oscarsCount, MovieGenre genre, MpaaRating mpaaRating, Person screenwriter) {
        return new Movie(id, name, coordinates, creationDate, oscarsCount, genre, mpaaRating, screenwriter);
    }

    /** Применяет частичное изменение: null — поле не передано; genre и screenwriter сбрасывает Optional.empty(). */
    public void apply(UpdateMovieRequest patch) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre newGenre = patch.getGenre() == null ? genre
                : EnumValues.parse(MovieGenre.class, patch.getGenre().orElse(null), FieldError.BODY, "genre", errors);
        MpaaRating newMpaaRating = patch.getMpaaRating() == null ? mpaaRating
                : EnumValues.parse(MpaaRating.class, patch.getMpaaRating(), FieldError.BODY, "mpaaRating", errors);
        Person newScreenwriter = patch.getScreenwriter() == null ? screenwriter
                : Person.from(patch.getScreenwriter().orElse(null), errors);
        EnumValues.throwIfAny(INVALID_BODY, errors);

        if (patch.getName() != null) {
            name = patch.getName();
        }
        if (patch.getCoordinates() != null) {
            coordinates = Coordinates.from(patch.getCoordinates());
        }
        if (patch.getOscarsCount() != null) {
            oscarsCount = patch.getOscarsCount();
        }
        genre = newGenre;
        mpaaRating = newMpaaRating;
        screenwriter = newScreenwriter;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Coordinates coordinates() {
        return coordinates;
    }

    public LocalDate creationDate() {
        return creationDate;
    }

    public int oscarsCount() {
        return oscarsCount;
    }

    public MovieGenre genre() {
        return genre;
    }

    public MpaaRating mpaaRating() {
        return mpaaRating;
    }

    public Person screenwriter() {
        return screenwriter;
    }
}
