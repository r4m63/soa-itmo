package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MovieRequestMapperTest {

    @Test
    void patchDistinguishesOmittedFieldsFromExplicitNull() {
        Movie movie = MovieRequestMapper.toMovie(new CreateMovieRequest(
                "Film", new CoordinatesDto(1, 2L), 3, "DRAMA", "R",
                new PersonDto("Writer", null, null, "FRANCE")));

        MovieRequestMapper.applyPatch(movie, new UpdateMovieRequest());
        assertEquals(MovieGenre.DRAMA, movie.genre());
        assertNotNull(movie.screenwriter());

        UpdateMovieRequest clear = new UpdateMovieRequest();
        clear.setGenre(Optional.empty());
        clear.setScreenwriter(Optional.empty());
        MovieRequestMapper.applyPatch(movie, clear);

        assertNull(movie.genre());
        assertNull(movie.screenwriter());
        assertEquals(3, movie.oscarsCount().value());
    }
}
