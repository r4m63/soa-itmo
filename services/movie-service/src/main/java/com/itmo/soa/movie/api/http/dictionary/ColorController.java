package com.itmo.soa.movie.api.http.dictionary;

import com.itmo.soa.contract.response.movie.DictionaryResponse;
import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;


@Path("/movies/colors")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ColorController {

    @Inject
    MovieService movieService;

    @GET
    public DictionaryResponse<Color> search(@QueryParam("search") String search) {
        return new DictionaryResponse<>(movieService.searchDictionary().execute(Color.class, search));
    }
}
