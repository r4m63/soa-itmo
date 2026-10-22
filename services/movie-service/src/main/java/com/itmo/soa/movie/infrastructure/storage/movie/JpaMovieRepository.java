package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.common.query.Page;
import com.itmo.soa.common.query.Query;
import com.itmo.soa.common.query.jpa.JpaQueryExecutor;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieEntity;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieGenre;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class JpaMovieRepository {

    @PersistenceContext(unitName = "movies")
    private EntityManager em;

    public MovieEntity insert(MovieEntity entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    public Optional<MovieEntity> findById(int id) {
        return Optional.ofNullable(em.find(MovieEntity.class, id));
    }

    public void delete(MovieEntity entity) {
        em.remove(entity);
    }

    public int deleteByGenre(MovieGenre genre) {
        return em.createQuery("DELETE FROM MovieEntity m WHERE m.genre = :genre")
                .setParameter("genre", genre)
                .executeUpdate();
    }

    public Page<MovieEntity> findPage(Query query) {
        return JpaQueryExecutor.execute(em, MovieEntity.class, query);
    }
}
