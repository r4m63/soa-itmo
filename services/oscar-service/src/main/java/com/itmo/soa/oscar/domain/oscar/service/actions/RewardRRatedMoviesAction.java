package com.itmo.soa.oscar.domain.oscar.service.actions;

import com.itmo.soa.contract.movie.MpaaRating;
import com.itmo.soa.contract.oscar.BulkOperationResult;
import com.itmo.soa.oscar.domain.oscar.mapper.AwardedMovieMapper;
import com.itmo.soa.oscar.domain.oscar.model.AwardedMovie;
import com.itmo.soa.oscar.domain.oscar.model.OscarsChange;
import com.itmo.soa.oscar.domain.oscar.service.OscarsChangeApplier;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardRRatedMoviesAction {

    private final MovieServiceClient movieServiceClient;
    private final OscarsChangeApplier oscarsChangeApplier;

    public RewardRRatedMoviesAction(MovieServiceClient movieServiceClient, OscarsChangeApplier oscarsChangeApplier) {
        this.movieServiceClient = movieServiceClient;
        this.oscarsChangeApplier = oscarsChangeApplier;
    }

    /** +1 Оскар каждому фильму рейтинга R. Удалённые во время операции фильмы не считаются. */
    public BulkOperationResult execute() {
        List<OscarsChange> changes = movieServiceClient.findAllByMpaaRating(MpaaRating.R).stream()
                .map(AwardedMovieMapper::toModel)
                .map(AwardedMovie::rewarded)
                .toList();
        return new BulkOperationResult(oscarsChangeApplier.apply(changes, true));
    }
}
