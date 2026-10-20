package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteMoviesByGenreAction {

    @Inject
    JpaMovieRepository movieRepository;

    /** Фильмы с genre = null не удаляются. */
    @Transactional
    public int execute(MovieGenre genre) {
        return movieRepository.deleteByGenre(genre);
    }
}
