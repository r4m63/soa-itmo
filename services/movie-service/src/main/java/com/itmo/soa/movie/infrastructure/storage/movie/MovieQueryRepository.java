package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieEntity;
import com.itmo.soa.query.Page;
import com.itmo.soa.query.Query;
import com.itmo.soa.query.jpa.JpaQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class MovieQueryRepository {

    @PersistenceContext(unitName = "movies")
    private EntityManager em;

    public Page<MovieEntity> findPage(Query query) {
        return JpaQueryExecutor.execute(em, MovieEntity.class, query);
    }
}
