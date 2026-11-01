package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.movie.domain.movie.model.Coordinates;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MovieName;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.itmo.soa.movie.domain.movie.model.OscarsCount;
import com.itmo.soa.movie.domain.movie.model.Person;

import java.util.Optional;

/**
 * Для genre и screenwriter null означает «поле не передано»,
 * а Optional.empty() — «очистить значение».
 */
public record PatchMovieCommand(
        MovieName name,
        Coordinates coordinates,
        OscarsCount oscarsCount,
        Optional<MovieGenre> genre,
        MpaaRating mpaaRating,
        Optional<Person> screenwriter
) {
}
