package com.itmo.soa.movie.config;

import com.itmo.soa.movie.infrastructure.storage.movie.entity.MovieEntity;
import com.wdbt.query.QueryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class QueryConfiguration {

    @Produces
    @Dependent
    public QueryService queryService() {
        return QueryService.forEntities(MovieEntity.class);
    }
}
