package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.service.MovieRepository;
import com.itmo.soa.movie.infrastructure.storage.movie.mapper.MovieEntityMapper;
import com.itmo.soa.movie.infrastructure.storage.movie.entity.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class MovieRepositoryAdapter implements MovieRepository {

    @Inject
    JpaMovieRepository movieRepository;

    @Override
    public Optional<Movie> findById(int id) {
        return movieRepository.findById(id).map(MovieEntityMapper::toModel);
    }

    @Override
    public Movie insert(Movie movie) {
        MovieEntity entity = MovieEntityMapper.toNewEntity(movie);
        return MovieEntityMapper.toModel(movieRepository.insert(entity));
    }

    @Override
    public Movie update(Movie movie) {
        MovieEntity entity = movieRepository.findById(movie.id())
                .orElseThrow(() -> new IllegalStateException("Фильм с id=" + movie.id() + " не найден"));
        MovieEntityMapper.copyTo(movie, entity);
        return MovieEntityMapper.toModel(movieRepository.update(entity));
    }

    @Override
    public void delete(Movie movie) {
        movieRepository.findById(movie.id()).ifPresent(movieRepository::delete);
    }

    @Override
    public int deleteByGenre(MovieGenre genre) {
        return movieRepository.deleteByGenre(genre);
    }
}
