package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.infrastructure.storage.movie.entity.MovieEntity;
import com.itmo.soa.movie.infrastructure.storage.movie.mapper.MovieEntityMapper;
import com.wdbt.query.model.Page;
import com.wdbt.query.model.Query;
import com.wdbt.query.parser.QueryParser;
import com.wdbt.query.parser.QueryValidationException;
import com.wdbt.query.jaxrs.QueryParameters;
import com.wdbt.query.jpa.JpaQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaMovieQueryRepository {

    private static final QueryParser QUERY_PARSER = new QueryParser(MovieQuerySchema.MOVIES);

    @PersistenceContext(unitName = "movies")
    private EntityManager em;

    public Page<Movie> findPage(QueryParameters parameters) {
        Query query;
        try {
            query = QUERY_PARSER.parse(parameters.filters(), parameters.sorts(), parameters.page(), parameters.size());
        } catch (QueryValidationException e) {
            throw new InvalidDataException(e.getMessage(), e.errors().stream()
                    .map(error -> FieldError.query(error.parameter(), error.rejectedValue(), error.message()))
                    .toList());
        }
        return JpaQueryExecutor.execute(em, MovieEntity.class, query).map(MovieEntityMapper::toModel);
    }
}
