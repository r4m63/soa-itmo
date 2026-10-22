package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.error.exception.NotFoundException;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteMovieAction {

    @Inject
    JpaMovieRepository movieRepository;

    @Transactional
    public void execute(int id) {
        movieRepository.delete(movieRepository.findById(id).orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден")));
    }
}
