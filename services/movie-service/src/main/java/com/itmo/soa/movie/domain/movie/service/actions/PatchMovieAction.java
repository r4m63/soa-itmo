package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.error.exception.NotFoundException;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import com.itmo.soa.movie.domain.movie.service.MovieRequestMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PatchMovieAction {

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public Movie execute(int id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        MovieRequestMapper.applyPatch(movie, request);
        return movieRepository.update(movie);
    }
}
