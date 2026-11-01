package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.infrastructure.storage.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.infrastructure.storage.movie.entity.MovieEntity;
import com.wdbt.query.Page;
import com.wdbt.query.Query;
import com.wdbt.query.QueryService;
import com.wdbt.query.QueryValidationException;
import com.wdbt.query.contract.MoviePageQuery;
import com.wdbt.query.jpa.JpaQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaMovieQueryRepository {

    @PersistenceContext(unitName = "movies")
    private EntityManager em;

    @Inject
    QueryService queryService;

    public Page<Movie> findPage(MoviePageQuery parameters) {
        Query query;
        try {
            query = queryService.parse(MovieEntity.class, parameters.filters(), parameters.sorts(),
                    parameters.page(), parameters.size());
        } catch (QueryValidationException e) {
            throw new InvalidDataException(e.getMessage(), e.errors().stream()
                    .map(error -> FieldError.query(error.parameter(), error.rejectedValue(), error.message()))
                    .toList());
        }
        return JpaQueryExecutor.execute(em, MovieEntity.class, query).map(MovieEntityMapper::toModel);
    }
}
