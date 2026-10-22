package com.itmo.soa.oscar.domain.oscar.mapper;

import com.itmo.soa.contract.response.movie.MovieResponse;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;

public final class AwardedMovieMapper {

    private AwardedMovieMapper() {
    }

    public static AwardedMovie toModel(MovieResponse movie) {
        return new AwardedMovie(movie.getId(), movie.getOscarsCount());
    }
}
