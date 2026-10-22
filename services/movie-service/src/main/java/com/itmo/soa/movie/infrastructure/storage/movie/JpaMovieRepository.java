package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieEntity;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieGenre;
import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

import java.util.Optional;


@Repository(dataStore = "movies")
public interface JpaMovieRepository extends DataRepository<MovieEntity, Integer> {

    @Insert
    MovieEntity insert(MovieEntity entity);

    @Find
    Optional<MovieEntity> findById(@By(By.ID) Integer id);

    @Update
    MovieEntity update(MovieEntity entity);

    @Delete
    void delete(MovieEntity entity);

    @Delete
    int deleteByGenre(@By("genre") MovieGenre genre);
}
