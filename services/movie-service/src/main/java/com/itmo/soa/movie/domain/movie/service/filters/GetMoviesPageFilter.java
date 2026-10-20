package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.movie.domain.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieQuerySchema;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieRepository;
import com.itmo.soa.query.Page;
import com.itmo.soa.query.QueryParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetMoviesPageFilter {

    private static final QueryParser PARSER = new QueryParser(MovieQuerySchema.MOVIES);

    @Inject
    JpaMovieRepository movieRepository;

    /** Неверные параметры — QueryValidationException со всеми ошибками. */
    public Page<Movie> execute(List<String> filters, List<String> sorts, String page, String size) {
        return movieRepository.findPage(PARSER.parse(filters, sorts, page, size)).map(MovieEntityMapper::toModel);
    }
}
