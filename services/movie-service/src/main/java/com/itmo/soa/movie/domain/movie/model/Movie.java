package com.itmo.soa.movie.domain.movie.model;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Агрегат «Фильм». Меняется только своими методами, каждый держит ограничения задания:
 * name, coordinates, oscarsCount, mpaaRating обязательны, creationDate ставит сам агрегат и больше не меняет.
 * id назначает хранилище.
 */
public final class Movie {

    private final Integer id;
    private final LocalDate creationDate;
    private MovieName name;
    private Coordinates coordinates;
    private OscarsCount oscarsCount;
    private MovieGenre genre;
    private MpaaRating mpaaRating;
    private Person screenwriter;

    private Movie(Integer id, LocalDate creationDate) {
        this.id = id;
        this.creationDate = required("creationDate", creationDate);
    }

    /** Новый фильм. Дата создания — сегодняшняя по clock. genre и screenwriter могут быть null. */
    public static Movie create(MovieName name, Coordinates coordinates, OscarsCount oscarsCount, MovieGenre genre,
                               MpaaRating mpaaRating, Person screenwriter, Clock clock) {
        Movie movie = new Movie(null, LocalDate.now(clock));
        movie.rename(name);
        movie.moveTo(coordinates);
        movie.changeOscars(oscarsCount);
        movie.changeGenre(genre);
        movie.rate(mpaaRating);
        movie.screenwriter = screenwriter;
        return movie;
    }

    /** Уже сохранённый фильм. */
    public static Movie restore(Integer id, LocalDate creationDate, MovieName name, Coordinates coordinates,
                                OscarsCount oscarsCount, MovieGenre genre, MpaaRating mpaaRating, Person screenwriter) {
        Movie movie = new Movie(id, creationDate);
        movie.rename(name);
        movie.moveTo(coordinates);
        movie.changeOscars(oscarsCount);
        movie.changeGenre(genre);
        movie.rate(mpaaRating);
        movie.screenwriter = screenwriter;
        return movie;
    }

    public void rename(MovieName name) {
        this.name = required("name", name);
    }

    public void moveTo(Coordinates coordinates) {
        this.coordinates = required("coordinates", coordinates);
    }

    public void changeOscars(OscarsCount oscarsCount) {
        this.oscarsCount = required("oscarsCount", oscarsCount);
    }

    /** null — фильм без жанра. */
    public void changeGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public void rate(MpaaRating mpaaRating) {
        this.mpaaRating = required("mpaaRating", mpaaRating);
    }

    public void assignScreenwriter(Person screenwriter) {
        this.screenwriter = required("screenwriter", screenwriter);
    }

    public void removeScreenwriter() {
        this.screenwriter = null;
    }

    private static <T> T required(String field, T value) {
        if (value == null) {
            throw new MovieRuleViolationException(field, null, "Поле обязательно");
        }
        return value;
    }

    public Integer id() {
        return id;
    }

    public LocalDate creationDate() {
        return creationDate;
    }

    public MovieName name() {
        return name;
    }

    public Coordinates coordinates() {
        return coordinates;
    }

    public OscarsCount oscarsCount() {
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
