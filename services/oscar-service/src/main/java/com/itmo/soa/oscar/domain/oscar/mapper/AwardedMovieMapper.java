package com.itmo.soa.oscar.domain.oscar.mapper;

import com.itmo.soa.contract.movie.Movie;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;

public final class AwardedMovieMapper {

    private AwardedMovieMapper() {
    }

    public static AwardedMovie toModel(Movie movie) {
        return new AwardedMovie(movie.id(), movie.oscarsCount());
    }
}
