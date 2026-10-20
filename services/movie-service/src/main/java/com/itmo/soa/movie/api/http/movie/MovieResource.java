package com.itmo.soa.movie.api.http.movie;

import com.itmo.soa.contract.error.FieldError;
import com.itmo.soa.contract.movie.DeletionResult;
import com.itmo.soa.contract.movie.Movie;
import com.itmo.soa.contract.movie.MovieInput;
import com.itmo.soa.contract.movie.MoviePage;
import com.itmo.soa.contract.movie.MoviePatch;
import com.itmo.soa.movie.api.http.PathParams;
import com.itmo.soa.movie.domain.movie.model.ValidationException;
import com.itmo.soa.movie.domain.movie.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/movies")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieService movieService;

    @GET
    public MoviePage list(@QueryParam("filter") List<String> filter,
                          @QueryParam("sort") List<String> sort,
                          @QueryParam("page") String page,
                          @QueryParam("size") String size) {
        return MovieResponseMapper.toResponse(movieService.getMoviesPage().execute(filter, sort, page, size));
    }

    @POST
    public Response create(MovieInput input) {
        Movie created = MovieResponseMapper.toResponse(movieService.createMovie().execute(requireBody(input)));
        return Response.created(URI.create("/movies/" + created.id())).entity(created).build();
    }

    @GET
    @Path("{id}")
    public Movie get(@PathParam("id") String id) {
        return MovieResponseMapper.toResponse(movieService.getMovieById().execute(PathParams.movieId(id)));
    }

    @PATCH
    @Path("{id}")
    public Movie patch(@PathParam("id") String id, MoviePatch patch) {
        int movieId = PathParams.movieId(id);
        return MovieResponseMapper.toResponse(movieService.patchMovie().execute(movieId, requireBody(patch)));
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        movieService.deleteMovie().execute(PathParams.movieId(id));
        return Response.noContent().build();
    }

    @DELETE
    @Path("by-genre/{genre}")
    public DeletionResult deleteByGenre(@PathParam("genre") String genre) {
        return new DeletionResult(movieService.deleteMoviesByGenre().execute(PathParams.genre(genre)));
    }

    /** При пустом теле JAX-RS может не вызвать reader и передать null. */
    private static <T> T requireBody(T body) {
        if (body == null) {
            throw new ValidationException("Некорректное тело запроса",
                    List.of(FieldError.body(null, null, "Тело запроса обязательно")));
        }
        return body;
    }
}
