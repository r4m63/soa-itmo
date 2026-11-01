package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;

import java.util.Optional;

public interface MovieRepository {

    Optional<Movie> findById(int id);

    Movie insert(Movie movie);

    Movie update(Movie movie);

    void delete(Movie movie);

    int deleteByGenre(MovieGenre genre);
}
