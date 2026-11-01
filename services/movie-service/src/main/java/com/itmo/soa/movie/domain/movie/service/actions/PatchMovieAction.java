package com.itmo.soa.movie.domain.movie.service.actions;

import com.itmo.soa.contract.error.exception.NotFoundException;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import com.itmo.soa.movie.domain.movie.service.PatchMovieCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PatchMovieAction {

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public Movie execute(int id, PatchMovieCommand command) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        applyPatch(movie, command);
        return movieRepository.update(movie);
    }

    private void applyPatch(Movie movie, PatchMovieCommand command) {
        if (command.name() != null) {
            movie.rename(command.name());
        }
        if (command.coordinates() != null) {
            movie.moveTo(command.coordinates());
        }
        if (command.oscarsCount() != null) {
            movie.changeOscars(command.oscarsCount());
        }
        if (command.genre() != null) {
            movie.changeGenre(command.genre().orElse(null));
        }
        if (command.mpaaRating() != null) {
            movie.rate(command.mpaaRating());
        }
        if (command.screenwriter() != null) {
            if (command.screenwriter().isEmpty()) {
                movie.removeScreenwriter();
            } else {
                movie.assignScreenwriter(command.screenwriter().orElseThrow());
            }
        }
    }
}
