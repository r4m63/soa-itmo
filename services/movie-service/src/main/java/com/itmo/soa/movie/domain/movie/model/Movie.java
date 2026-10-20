package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.contract.common.PatchField;
import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.contract.movie.MovieInput;
import com.itmo.soa.contract.movie.MoviePatch;
import com.itmo.soa.contract.movie.MpaaRating;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * Агрегат «Фильм». Любое изменение проходит через create или apply, поэтому ограничения задания всегда соблюдены.
 * id и creationDate задаются при создании и больше не меняются.
 */
public final class Movie {

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

    /** Новый фильм из запроса. id назначит хранилище. */
    public static Movie create(MovieInput input, LocalDate creationDate) {
        Violations violations = new Violations();
        String name = name(input.name(), violations);
        Coordinates coordinates = Coordinates.from(input.coordinates(), violations);
        Integer oscarsCount = oscarsCount(input.oscarsCount(), violations);
        MpaaRating mpaaRating = mpaaRating(input.mpaaRating(), violations);
        Person screenwriter = Person.from(input.screenwriter(), violations);
        violations.throwIfAny();
        return new Movie(null, name, coordinates, creationDate, oscarsCount, input.genre(), mpaaRating, screenwriter);
    }

    /** Восстановление уже сохранённого фильма, без проверок. */
    public static Movie restore(Integer id, String name, Coordinates coordinates, LocalDate creationDate,
                                int oscarsCount, MovieGenre genre, MpaaRating mpaaRating, Person screenwriter) {
        return new Movie(id, name, coordinates, creationDate, oscarsCount, genre, mpaaRating, screenwriter);
    }

    /** Применяет частичное изменение: только переданные поля, null сбрасывает nullable-поле. */
    public void apply(MoviePatch patch) {
        Violations violations = new Violations();
        String newName = patched(patch.name(), name, value -> name(value, violations));
        Coordinates newCoordinates = patched(patch.coordinates(), coordinates,
                value -> Coordinates.from(value, violations));
        Integer newOscarsCount = patched(patch.oscarsCount(), oscarsCount, value -> oscarsCount(value, violations));
        MpaaRating newMpaaRating = patched(patch.mpaaRating(), mpaaRating, value -> mpaaRating(value, violations));
        Person newScreenwriter = patched(patch.screenwriter(), screenwriter, value -> Person.from(value, violations));
        violations.throwIfAny();

        name = newName;
        coordinates = newCoordinates;
        oscarsCount = newOscarsCount;
        genre = patch.genre().orElse(genre);
        mpaaRating = newMpaaRating;
        screenwriter = newScreenwriter;
    }

    private static <D, T> T patched(PatchField<D> field, T current, Function<D, T> validate) {
        return field.isPresent() ? validate.apply(field.value()) : current;
    }

    private static String name(String value, Violations violations) {
        if (value == null) {
            violations.add("name", null, Violations.REQUIRED);
        } else if (value.isBlank()) {
            violations.add("name", value, "Название не может быть пустым");
        }
        return value;
    }

    private static Integer oscarsCount(Integer value, Violations violations) {
        if (value == null) {
            violations.add("oscarsCount", null, Violations.REQUIRED);
        } else if (value <= 0) {
            violations.add("oscarsCount", value, "Значение должно быть больше 0");
        }
        return value;
    }

    private static MpaaRating mpaaRating(MpaaRating value, Violations violations) {
        if (value == null) {
            violations.add("mpaaRating", null, Violations.REQUIRED);
        }
        return value;
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
