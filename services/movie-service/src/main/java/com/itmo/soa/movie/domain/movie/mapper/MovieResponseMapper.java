package com.itmo.soa.movie.domain.movie.mapper;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.response.movie.MoviePageResponse;
import com.itmo.soa.contract.response.movie.MovieResponse;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.query.Page;

public final class MovieResponseMapper {

    private MovieResponseMapper() {
    }

    public static MovieResponse toResponse(Movie movie) {
        PersonDto screenwriter = movie.screenwriter() == null ? null : new PersonDto(
                movie.screenwriter().name(),
                movie.screenwriter().passportID(),
                name(movie.screenwriter().hairColor()),
                name(movie.screenwriter().nationality()));
        return new MovieResponse(
                movie.id(),
                movie.name(),
                new CoordinatesDto(movie.coordinates().x(), movie.coordinates().y()),
                movie.creationDate(),
                movie.oscarsCount(),
                name(movie.genre()),
                name(movie.mpaaRating()),
                screenwriter);
    }

    public static MoviePageResponse toResponse(Page<Movie> page) {
        return new MoviePageResponse(
                page.items().stream().map(MovieResponseMapper::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
