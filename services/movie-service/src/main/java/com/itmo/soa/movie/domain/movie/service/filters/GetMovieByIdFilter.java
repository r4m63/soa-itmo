package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.common.exception.NotFoundException;
import com.itmo.soa.movie.domain.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMovieByIdFilter {

    @Inject
    JpaMovieRepository movieRepository;

    public Movie execute(int id) {
        return movieRepository.findById(id)
                .map(MovieEntityMapper::toModel)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
