package com.itmo.soa.movie.api.http.dictionary;

import com.itmo.soa.contract.response.movie.DictionaryResponse;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.itmo.soa.movie.domain.movie.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;


@Path("/movies/mpaa-ratings")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class MpaaRatingController {

    @Inject
    MovieService movieService;

    @GET
    public DictionaryResponse<MpaaRating> search(@QueryParam("search") String search) {
        return new DictionaryResponse<>(movieService.searchDictionary().execute(MpaaRating.class, search));
    }
}
