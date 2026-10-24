package com.itmo.soa.oscar.domain.oscar.service;

import com.itmo.soa.oscar.domain.oscar.model.OscarsChange;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieGoneException;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class OscarsChangeApplier {

    private static final Logger log = LoggerFactory.getLogger(OscarsChangeApplier.class);

    private final MovieServiceClient movieServiceClient;

    public OscarsChangeApplier(MovieServiceClient movieServiceClient) {
        this.movieServiceClient = movieServiceClient;
    }

    public int apply(List<OscarsChange> changes, boolean skipGoneMovies) {
        Deque<OscarsChange> applied = new ArrayDeque<>();
        try {
            for (OscarsChange change : changes) {
                try {
                    movieServiceClient.setOscarsCount(change.movieId(), change.to());
                    applied.push(change);
                } catch (MovieGoneException e) {
                    if (!skipGoneMovies) {
                        throw e;
                    }
                    log.info("Фильм {} удалён во время операции, пропущен", change.movieId());
                }
            }
            return applied.size();
        } catch (RuntimeException e) {
            rollback(applied);
            throw e;
        }
    }

    private void rollback(Deque<OscarsChange> applied) {
        while (!applied.isEmpty()) {
            OscarsChange change = applied.pop();
            try {
                movieServiceClient.setOscarsCount(change.movieId(), change.from());
            } catch (RuntimeException e) {
                log.error("Не удалось откатить награды фильма {}: должно быть {}", change.movieId(), change.from(), e);
            }
        }
    }
}
