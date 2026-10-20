package com.itmo.soa.movie.api.http.dictionary;

import com.itmo.soa.contract.common.ItemList;
import com.itmo.soa.contract.movie.Color;
import com.itmo.soa.contract.movie.Country;
import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.contract.movie.MpaaRating;
import com.itmo.soa.movie.domain.movie.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Справочники enum. Путь с перечислением имён: в отличие от /movies/{id}, он совпадает только с ними.
 */
@Path("/movies/{dictionary: genres|mpaa-ratings|colors|countries}")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class DictionaryResource {

    @Inject
    MovieService movieService;

    @GET
    public ItemList<?> search(@PathParam("dictionary") String dictionary, @QueryParam("search") String search) {
        List<?> items = switch (dictionary) {
            case "genres" -> movieService.searchDictionary().execute(MovieGenre.class, search);
            case "mpaa-ratings" -> movieService.searchDictionary().execute(MpaaRating.class, search);
            case "colors" -> movieService.searchDictionary().execute(Color.class, search);
            case "countries" -> movieService.searchDictionary().execute(Country.class, search);
            default -> throw new IllegalStateException("Неизвестный справочник " + dictionary);
        };
        return new ItemList<>(items);
    }
}
