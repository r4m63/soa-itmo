package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.movie.domain.movie.model.Coordinates;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MovieName;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.itmo.soa.movie.domain.movie.model.OscarsCount;
import com.itmo.soa.movie.domain.movie.model.Person;

public record CreateMovieCommand(
        MovieName name,
        Coordinates coordinates,
        OscarsCount oscarsCount,
        MovieGenre genre,
        MpaaRating mpaaRating,
        Person screenwriter
) {
}
