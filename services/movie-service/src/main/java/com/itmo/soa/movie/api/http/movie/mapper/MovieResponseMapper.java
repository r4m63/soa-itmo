package com.itmo.soa.movie.api.http.movie.mapper;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.response.movie.MoviePageResponse;
import com.itmo.soa.contract.response.movie.MovieResponse;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.wdbt.query.model.Page;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class MovieResponseMapper {

    public static MovieResponse toResponse(Movie movie) {
        PersonDto screenwriter = movie.screenwriter() == null ? null : PersonDto.builder()
                .name(movie.screenwriter().name())
                .passportID(movie.screenwriter().passportID())
                .hairColor(name(movie.screenwriter().hairColor()))
                .nationality(name(movie.screenwriter().nationality()))
                .build();
        return MovieResponse.builder()
                .id(movie.id())
                .name(movie.name().value())
                .coordinates(CoordinatesDto.builder()
                        .x(movie.coordinates().x())
                        .y(movie.coordinates().y())
                        .build())
                .creationDate(movie.creationDate())
                .oscarsCount(movie.oscarsCount().value())
                .genre(name(movie.genre()))
                .mpaaRating(name(movie.mpaaRating()))
                .screenwriter(screenwriter)
                .build();
    }

    public static MoviePageResponse toResponse(Page<Movie> page) {
        return MoviePageResponse.builder()
                .items(page.items().stream().map(MovieResponseMapper::toResponse).toList())
                .page(page.page())
                .size(page.size())
                .totalElements(page.totalElements())
                .totalPages(page.totalPages())
                .build();
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
