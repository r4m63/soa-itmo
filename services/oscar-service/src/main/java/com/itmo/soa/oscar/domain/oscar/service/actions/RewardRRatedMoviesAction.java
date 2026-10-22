package com.itmo.soa.oscar.domain.oscar.service.actions;

import com.itmo.soa.contract.response.oscar.RewardRRatedMoviesResponse;
import com.itmo.soa.oscar.domain.oscar.mapper.AwardedMovieMapper;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;
import com.itmo.soa.oscar.domain.oscar.model.OscarsChange;
import com.itmo.soa.oscar.domain.oscar.service.OscarsChangeApplier;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardRRatedMoviesAction {

    private static final String R_RATING = "R";

    private final MovieServiceClient movieServiceClient;
    private final OscarsChangeApplier oscarsChangeApplier;

    public RewardRRatedMoviesAction(MovieServiceClient movieServiceClient, OscarsChangeApplier oscarsChangeApplier) {
        this.movieServiceClient = movieServiceClient;
        this.oscarsChangeApplier = oscarsChangeApplier;
    }

    /** +1 Оскар каждому фильму рейтинга R. Удалённые во время операции фильмы не считаются. */
    public RewardRRatedMoviesResponse execute() {
        List<OscarsChange> changes = movieServiceClient.findAllByMpaaRating(R_RATING).stream()
                .map(AwardedMovieMapper::toModel)
                .map(AwardedMovie::rewarded)
                .toList();
        return new RewardRRatedMoviesResponse(oscarsChangeApplier.apply(changes, true));
    }
}
