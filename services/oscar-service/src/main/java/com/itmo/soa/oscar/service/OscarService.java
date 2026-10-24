package com.itmo.soa.oscar.service;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.exception.InvalidOperationException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.response.movie.MovieResponse;
import com.itmo.soa.contract.response.oscar.RedistributeOscarsResponse;
import com.itmo.soa.contract.response.oscar.RewardRRatedMoviesResponse;
import com.itmo.soa.oscar.client.MovieGoneException;
import com.itmo.soa.oscar.client.MovieServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

@Service
public class OscarService {

    private static final Logger log = LoggerFactory.getLogger(OscarService.class);

    private static final String R_RATING = "R";

    private final MovieServiceClient movieServiceClient;

    public OscarService(MovieServiceClient movieServiceClient) {
        this.movieServiceClient = movieServiceClient;
    }

    public RewardRRatedMoviesResponse rewardRRatedMovies() {
        List<OscarsChange> changes = new ArrayList<>();
        for (MovieResponse movie : movieServiceClient.findAllByMpaaRating(R_RATING)) {
            int oscarsCount = oscarsCount(movie);
            changes.add(new OscarsChange(movie.getId(), oscarsCount, Math.addExact(oscarsCount, 1)));
        }
        return new RewardRRatedMoviesResponse(applyChanges(changes, true));
    }

    public RedistributeOscarsResponse redistributeOscars(String from, String to) {
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

        List<MovieResponse> sources = moviesByGenre(from);
        List<MovieResponse> targets = moviesByGenre(to);
        if (targets.isEmpty()) {
            throw new InvalidOperationException("В жанре " + to + " нет фильмов для получения наград");
        }

        List<OscarsChange> changes = new ArrayList<>();
        long transferred = 0;
        for (MovieResponse source : sources) {
            int oscarsCount = source.getOscarsCount();
            if (oscarsCount > 1) {
                transferred += oscarsCount - 1L;
                changes.add(new OscarsChange(source.getId(), oscarsCount, 1));
            }
        }

        List<MovieResponse> targetsById = targets.stream().sorted(Comparator.comparingInt(MovieResponse::getId)).toList();
        long share = transferred / targetsById.size();
        long remainder = transferred % targetsById.size();
        for (int i = 0; i < targetsById.size(); i++) {
            MovieResponse target = targetsById.get(i);
            long added = share + (i < remainder ? 1 : 0);
            if (added > 0) {
                changes.add(new OscarsChange(target.getId(), target.getOscarsCount(),
                        Math.toIntExact(target.getOscarsCount() + added)));
            }
        }

        applyChanges(changes, false);
        return new RedistributeOscarsResponse(sources.size(), targets.size(), transferred);
    }

    private List<MovieResponse> moviesByGenre(String genre) {
        List<MovieResponse> movies = movieServiceClient.findAllByGenre(genre);
        movies.forEach(OscarService::oscarsCount);
        return movies;
    }

    private static int oscarsCount(MovieResponse movie) {
        int oscarsCount = movie.getOscarsCount();
        if (oscarsCount < 1) {
            throw new IllegalStateException("У фильма " + movie.getId() + " oscarsCount = " + oscarsCount + ", ожидается не меньше 1");
        }
        return oscarsCount;
    }

    private static void checkGenre(String parameter, String genre, List<String> genres, List<FieldError> errors) {
        if (!genres.contains(genre)) {
            errors.add(FieldError.path(parameter, genre, "Допустимые значения: " + String.join(", ", genres)));
        }
    }

    private int applyChanges(List<OscarsChange> changes, boolean skipGoneMovies) {
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

    private record OscarsChange(int movieId, int from, int to) {
    }
}
