package com.itmo.soa.movie.domain.movie.mapper;

import com.itmo.soa.movie.domain.movie.model.Coordinates;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.model.MovieName;
import com.itmo.soa.movie.domain.movie.model.OscarsCount;
import com.itmo.soa.movie.domain.movie.model.Person;
import com.itmo.soa.movie.infrastructure.storage.movie.model.CoordinatesEmbeddable;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieEntity;
import com.itmo.soa.movie.infrastructure.storage.movie.model.ScreenwriterEmbeddable;


/**
 * MovieEntity ⇄ Movie. Лежит в domain: infrastructure о доменной модели не знает.
 */
public final class MovieEntityMapper {

    private MovieEntityMapper() {
    }

    public static Movie toModel(MovieEntity entity) {
        CoordinatesEmbeddable coordinates = entity.getCoordinates();
        return Movie.restore(
                entity.getId(),
                entity.getCreationDate(),
                new MovieName(entity.getName()),
                new Coordinates(coordinates.getX(), coordinates.getY()),
                new OscarsCount(entity.getOscarsCount()),
                entity.getGenre(),
                entity.getMpaaRating(),
                toModel(entity.getScreenwriter())
        );
    }

    public static MovieEntity toNewEntity(Movie movie) {
        MovieEntity entity = new MovieEntity();
        entity.setCreationDate(movie.creationDate());
        copyTo(movie, entity);
        return entity;
    }

    /** Переносит изменяемые поля; id и creationDate не трогает. */
    public static void copyTo(Movie movie, MovieEntity entity) {
        entity.setName(movie.name().value());
        entity.setCoordinates(toEmbeddable(movie.coordinates()));
        entity.setOscarsCount(movie.oscarsCount().value());
        entity.setGenre(movie.genre());
        entity.setMpaaRating(movie.mpaaRating());
        entity.setScreenwriter(toEmbeddable(movie.screenwriter()));
    }

    private static Person toModel(ScreenwriterEmbeddable screenwriter) {
        // имя сценариста обязательно: пустой embeddable означает «сценариста нет»
        if (screenwriter == null || screenwriter.getName() == null) {
            return null;
        }
        return new Person(screenwriter.getName(), screenwriter.getPassportID(), screenwriter.getHairColor(),
                screenwriter.getNationality());
    }

    private static CoordinatesEmbeddable toEmbeddable(Coordinates coordinates) {
        CoordinatesEmbeddable embeddable = new CoordinatesEmbeddable();
        embeddable.setX(coordinates.x());
        embeddable.setY(coordinates.y());
        return embeddable;
    }

    private static ScreenwriterEmbeddable toEmbeddable(Person person) {
        if (person == null) {
            return null;
        }
        ScreenwriterEmbeddable embeddable = new ScreenwriterEmbeddable();
        embeddable.setName(person.name());
        embeddable.setPassportID(person.passportID());
        embeddable.setHairColor(person.hairColor());
        embeddable.setNationality(person.nationality());
        return embeddable;
    }
}
