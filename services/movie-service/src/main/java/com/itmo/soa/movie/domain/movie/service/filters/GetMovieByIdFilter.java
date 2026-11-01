package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.contract.error.exception.NotFoundException;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMovieByIdFilter {

    @Inject
    MovieRepository movieRepository;

    public Movie execute(int id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
