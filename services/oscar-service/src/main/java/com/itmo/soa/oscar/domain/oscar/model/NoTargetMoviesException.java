package com.itmo.soa.oscar.domain.oscar.model;

import com.itmo.soa.contract.movie.MovieGenre;

public class NoTargetMoviesException extends RuntimeException {

    public NoTargetMoviesException(MovieGenre genre) {
        super("В жанре " + genre + " нет фильмов для получения наград");
    }
}
