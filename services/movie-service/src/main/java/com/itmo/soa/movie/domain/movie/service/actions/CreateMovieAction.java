package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.CreateMovieCommand;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateMovieAction {

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public Movie execute(CreateMovieCommand command) {
        Movie movie = Movie.create(command.name(), command.coordinates(), command.oscarsCount(),
                command.genre(), command.mpaaRating(), command.screenwriter());
        return movieRepository.insert(movie);
    }
}
