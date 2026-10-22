package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.movie.domain.movie.mapper.MovieRequestMapper;
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
    public int execute(String genre) {
        return movieRepository.deleteByGenre(MovieRequestMapper.genreFromPath(genre));
    }
}
