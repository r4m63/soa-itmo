package com.itmo.soa.movie.api.http.dictionary;

import com.itmo.soa.contract.common.dictionary.DictionaryResponse;
import com.itmo.soa.movie.domain.movie.service.MovieService;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieGenre;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;


@Path("/movies/genres")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class GenreController {

    @Inject
    MovieService movieService;

    @GET
    public DictionaryResponse<MovieGenre> search(@QueryParam("search") String search) {
        return new DictionaryResponse<>(movieService.searchDictionary().execute(MovieGenre.class, search));
    }
}
