package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import com.itmo.soa.movie.domain.movie.service.MovieRequestMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateMovieAction {

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public Movie execute(CreateMovieRequest request) {
        return movieRepository.insert(MovieRequestMapper.toMovie(request));
    }
}
