package com.itmo.soa.movie.api.http.movie;

import com.itmo.soa.contract.movie.Coordinates;
import com.itmo.soa.contract.movie.MoviePage;
import com.itmo.soa.contract.movie.Person;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.query.Page;

public final class MovieResponseMapper {

    private MovieResponseMapper() {
    }

    public static com.itmo.soa.contract.movie.Movie toResponse(Movie movie) {
        Person screenwriter = movie.screenwriter() == null ? null : new Person(
                movie.screenwriter().name(),
                movie.screenwriter().passportID(),
                movie.screenwriter().hairColor(),
                movie.screenwriter().nationality());
        return new com.itmo.soa.contract.movie.Movie(
                movie.id(),
                movie.name(),
                new Coordinates(movie.coordinates().x(), movie.coordinates().y()),
                movie.creationDate(),
                movie.oscarsCount(),
                movie.genre(),
                movie.mpaaRating(),
                screenwriter);
    }

    public static MoviePage toResponse(Page<Movie> page) {
        return new MoviePage(
                page.items().stream().map(MovieResponseMapper::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }
}
