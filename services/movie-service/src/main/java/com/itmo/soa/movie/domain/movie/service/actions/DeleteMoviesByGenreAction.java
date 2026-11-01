package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteMoviesByGenreAction {

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public int execute(MovieGenre genre) {
        return movieRepository.deleteByGenre(genre);
    }
}
