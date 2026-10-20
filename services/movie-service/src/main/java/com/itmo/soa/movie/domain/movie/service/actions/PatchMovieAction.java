package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.movie.MoviePatch;
import com.itmo.soa.movie.domain.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.model.MovieNotFoundException;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PatchMovieAction {

    @Inject
    JpaMovieRepository movieRepository;

    @Transactional
    public Movie execute(int id, MoviePatch patch) {
        MovieEntity entity = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        Movie movie = MovieEntityMapper.toModel(entity);
        movie.apply(patch);
        MovieEntityMapper.copyTo(movie, entity);
        return movie;
    }
}
