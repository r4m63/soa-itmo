package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.error.exception.NotFoundException;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteMovieAction {

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public void execute(int id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        movieRepository.delete(movie);
    }
}
