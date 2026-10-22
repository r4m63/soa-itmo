package com.itmo.soa.oscar.domain.oscar.service.actions;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.response.oscar.RedistributeOscarsResponse;
import com.itmo.soa.oscar.domain.oscar.mapper.AwardedMovieMapper;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;
import com.itmo.soa.oscar.domain.oscar.model.RedistributionPlan;
import com.itmo.soa.oscar.domain.oscar.service.OscarsChangeApplier;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieServiceClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedistributeOscarsAction {

    private final MovieServiceClient movieServiceClient;
    private final OscarsChangeApplier oscarsChangeApplier;

    public RedistributeOscarsAction(MovieServiceClient movieServiceClient, OscarsChangeApplier oscarsChangeApplier) {
        this.movieServiceClient = movieServiceClient;
        this.oscarsChangeApplier = oscarsChangeApplier;
    }

    /**
     * Сначала всё проверяется и считается, потом применяется: 400 и 409 — до первого изменения.
     */
    public RedistributeOscarsResponse execute(String from, String to) {
        List<String> genres = movieServiceClient.findAllGenres();
        List<FieldError> errors = new ArrayList<>();
        checkGenre("from-genre", from, genres, errors);
        checkGenre("to-genre", to, genres, errors);
        if (errors.isEmpty() && from.equals(to)) {
            errors.add(FieldError.path("to-genre", to, "Жанр назначения должен отличаться от исходного"));
        }
        if (!errors.isEmpty()) {
            throw new InvalidDataException("Некорректные параметры запроса", errors);
        }

        List<AwardedMovie> sources = movies(from);
        List<AwardedMovie> targets = movies(to);
        RedistributionPlan plan = RedistributionPlan.calculate(to, sources, targets);
        oscarsChangeApplier.apply(plan.changes(), false);
        return new RedistributeOscarsResponse(plan.sourceMovies(), plan.targetMovies(), plan.transferredOscars());
    }

    private List<AwardedMovie> movies(String genre) {
        return movieServiceClient.findAllByGenre(genre).stream().map(AwardedMovieMapper::toModel).toList();
    }

    private static void checkGenre(String parameter, String genre, List<String> genres, List<FieldError> errors) {
        if (!genres.contains(genre)) {
            errors.add(FieldError.path(parameter, genre, "Допустимые значения: " + String.join(", ", genres)));
        }
    }
}
