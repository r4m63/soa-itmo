package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieQueryRepository;
import com.wdbt.query.model.Page;
import com.wdbt.query.jaxrs.QueryParameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMoviesPageFilter {

    @Inject
    JpaMovieQueryRepository movieQueryRepository;

    public Page<Movie> execute(QueryParameters query) {
        return movieQueryRepository.findPage(query);
    }
}
