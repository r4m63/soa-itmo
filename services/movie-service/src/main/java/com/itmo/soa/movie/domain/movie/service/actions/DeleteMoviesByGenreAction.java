package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.movie.domain.movie.model.EnumValues;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieGenre;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DeleteMoviesByGenreAction {

    @Inject
    JpaMovieRepository movieRepository;

    /** Фильмы с genre = null не удаляются. */
    @Transactional
    public int execute(String genre) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre value = EnumValues.parse(MovieGenre.class, genre, FieldError.PATH, "genre", errors);
        EnumValues.throwIfAny("Некорректные параметры запроса", errors);
        return movieRepository.deleteByGenre(value);
    }
}
