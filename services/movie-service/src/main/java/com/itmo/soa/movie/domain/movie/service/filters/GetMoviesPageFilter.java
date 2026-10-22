package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.movie.domain.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.service.MovieQuerySchema;
import com.itmo.soa.movie.infrastructure.storage.movie.MovieQueryRepository;
import com.itmo.soa.query.Page;
import com.itmo.soa.query.Query;
import com.itmo.soa.query.QueryParser;
import com.itmo.soa.query.QueryValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetMoviesPageFilter {

    private static final QueryParser PARSER = new QueryParser(MovieQuerySchema.MOVIES);

    @Inject
    MovieQueryRepository movieQueryRepository;

    /** Неверные параметры — InvalidDataException со всеми ошибками filter[i], sort[i], page, size. */
    public Page<Movie> execute(List<String> filters, List<String> sorts, String page, String size) {
        Query query;
        try {
            query = PARSER.parse(filters, sorts, page, size);
        } catch (QueryValidationException e) {
            throw new InvalidDataException(e.getMessage(), e.errors().stream()
                    .map(error -> FieldError.query(error.parameter(), error.rejectedValue(), error.message()))
                    .toList());
        }
        return movieQueryRepository.findPage(query).map(MovieEntityMapper::toModel);
    }
}
