package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieEntity;
import com.itmo.soa.query.Page;
import com.itmo.soa.query.Query;
import com.itmo.soa.query.jpa.JpaQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class JpaMovieRepository {

    @PersistenceContext(unitName = "movies")
    private EntityManager em;

    /** Сохраняет новый фильм и сразу получает id от базы. */
    public MovieEntity insert(MovieEntity entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    /** Внутри транзакции возвращает управляемую сущность: изменения сохранятся при коммите. */
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
