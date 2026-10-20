package com.itmo.soa.oscar.domain.oscar.service.actions;

import com.itmo.soa.contract.error.FieldError;
import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.contract.oscar.RedistributionResult;
import com.itmo.soa.oscar.domain.oscar.mapper.AwardedMovieMapper;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;
import com.itmo.soa.oscar.domain.oscar.model.InvalidGenreException;
import com.itmo.soa.oscar.domain.oscar.model.RedistributionPlan;
import com.itmo.soa.oscar.domain.oscar.service.OscarsChangeApplier;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieServiceClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public RedistributionResult execute(String fromGenre, String toGenre) {
        List<FieldError> errors = new ArrayList<>();
        MovieGenre from = genre("from-genre", fromGenre, errors);
        MovieGenre to = genre("to-genre", toGenre, errors);
        if (errors.isEmpty() && from == to) {
            errors.add(FieldError.path("to-genre", toGenre, "Жанр назначения должен отличаться от исходного"));
        }
        if (!errors.isEmpty()) {
            throw new InvalidGenreException(errors);
        }

        List<AwardedMovie> sources = movies(from);
        List<AwardedMovie> targets = movies(to);
        RedistributionPlan plan = RedistributionPlan.calculate(to, sources, targets);
        oscarsChangeApplier.apply(plan.changes(), false);
        return new RedistributionResult(plan.sourceMovies(), plan.targetMovies(), plan.transferredOscars());
    }

    private List<AwardedMovie> movies(MovieGenre genre) {
        return movieServiceClient.findAllByGenre(genre).stream().map(AwardedMovieMapper::toModel).toList();
    }

    private static MovieGenre genre(String parameter, String raw, List<FieldError> errors) {
        for (MovieGenre genre : MovieGenre.values()) {
            if (genre.name().equals(raw)) {
                return genre;
            }
        }
        errors.add(FieldError.path(parameter, raw, "Допустимые значения: " + Arrays.stream(MovieGenre.values())
                .map(Enum::name).collect(Collectors.joining(", "))));
        return null;
    }
}
