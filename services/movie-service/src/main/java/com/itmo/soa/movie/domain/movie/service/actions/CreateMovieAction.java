package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.movie.domain.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.domain.movie.mapper.MovieRequestMapper;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

@ApplicationScoped
public class CreateMovieAction {

    @Inject
    JpaMovieRepository movieRepository;

    @Inject
    Clock clock;

    @Transactional
    public Movie execute(CreateMovieRequest input) {
        Movie movie = MovieRequestMapper.toNewMovie(input, clock);
        return MovieEntityMapper.toModel(movieRepository.insert(MovieEntityMapper.toNewEntity(movie)));
    }
}
