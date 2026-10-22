package com.itmo.soa.oscar.domain.oscar.mapper;

import com.itmo.soa.contract.movie.movie.response.MovieResponse;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;

public final class AwardedMovieMapper {

    private AwardedMovieMapper() {
    }

    public static AwardedMovie toModel(MovieResponse movie) {
        return new AwardedMovie(movie.getId(), movie.getOscarsCount());
    }
}
