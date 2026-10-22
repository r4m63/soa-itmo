package com.itmo.soa.movie.api.http.movie;

import com.itmo.soa.contract.movie.movie.request.CreateMovieRequest;
import com.itmo.soa.contract.movie.movie.request.MovieGenrePathParams;
import com.itmo.soa.contract.movie.movie.request.MovieIdPathParams;
import com.itmo.soa.contract.movie.movie.request.UpdateMovieRequest;
import com.itmo.soa.contract.movie.movie.response.DeleteMoviesByGenreResponse;
import com.itmo.soa.contract.movie.movie.response.MoviePageResponse;
import com.itmo.soa.contract.movie.movie.response.MovieResponse;
import com.itmo.soa.movie.domain.movie.mapper.MovieResponseMapper;
import com.itmo.soa.movie.domain.movie.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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
public class MovieController {

    @Inject
    MovieService movieService;

    @GET
    public MoviePageResponse list(@QueryParam("filter") List<String> filter,
                          @QueryParam("sort") List<String> sort,
                          @QueryParam("page") String page,
                          @QueryParam("size") String size) {
        return MovieResponseMapper.toResponse(movieService.getMoviesPage().execute(filter, sort, page, size));
    }

    @POST
    public Response create(@Valid CreateMovieRequest input) {
        MovieResponse created = MovieResponseMapper.toResponse(movieService.createMovie().execute(input));
        return Response.created(URI.create("/movies/" + created.getId())).entity(created).build();
    }

    @GET
    @Path("{id}")
    public MovieResponse get(@Valid @BeanParam MovieIdPathParams params) {
        return MovieResponseMapper.toResponse(movieService.getMovieById().execute(params.getId()));
    }

    @PATCH
    @Path("{id}")
    public MovieResponse patch(@Valid @BeanParam MovieIdPathParams params, @Valid UpdateMovieRequest patch) {
        return MovieResponseMapper.toResponse(movieService.patchMovie().execute(params.getId(), patch));
    }

    @DELETE
    @Path("{id}")
    public Response delete(@Valid @BeanParam MovieIdPathParams params) {
        movieService.deleteMovie().execute(params.getId());
        return Response.noContent().build();
    }

    @DELETE
    @Path("by-genre/{genre}")
    public DeleteMoviesByGenreResponse deleteByGenre(@BeanParam MovieGenrePathParams params) {
        return new DeleteMoviesByGenreResponse(movieService.deleteMoviesByGenre().execute(params.getGenre()));
    }
}
