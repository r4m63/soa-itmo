package com.itmo.soa.oscar.client;

import com.itmo.soa.contract.error.exception.DownstreamServiceUnavailableException;
import com.itmo.soa.contract.response.movie.DictionaryResponse;
import com.itmo.soa.contract.response.movie.MoviePageResponse;
import com.itmo.soa.contract.response.movie.MovieResponse;
import com.itmo.soa.query.Direction;
import com.itmo.soa.query.Filter;
import com.itmo.soa.query.Operator;
import com.itmo.soa.query.Sort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class MovieServiceClient {

    private static final int PAGE_SIZE = 100;
    private static final String UNAVAILABLE = "Movie Service недоступен";
    private static final String SORT_BY_ID = Sort.format("id", Direction.ASC);

    private final RestClient movieServiceRestClient;

    public MovieServiceClient(RestClient movieServiceRestClient) {
        this.movieServiceRestClient = movieServiceRestClient;
    }

    public List<MovieResponse> findAllByMpaaRating(String mpaaRating) {
        return findAll(Filter.format("mpaaRating", Operator.EQ, mpaaRating));
    }

    public List<MovieResponse> findAllByGenre(String genre) {
        return findAll(Filter.format("genre", Operator.EQ, genre));
    }

    public List<String> findAllGenres() {
        DictionaryResponse<String> genres = call(() -> movieServiceRestClient.get()
                .uri("/movies/genres")
                .retrieve()
                .body(new ParameterizedTypeReference<DictionaryResponse<String>>() {
                }));
        if (genres == null) {
            throw new DownstreamServiceUnavailableException(UNAVAILABLE, new IllegalStateException("пустой ответ"));
        }
        return genres.getItems();
    }

    public void setOscarsCount(int movieId, int oscarsCount) {
        try {
            call(() -> movieServiceRestClient.patch()
                    .uri("/movies/{id}", movieId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("oscarsCount", oscarsCount))
                    .retrieve()
                    .toBodilessEntity());
        } catch (HttpClientErrorException.NotFound e) {
            throw new MovieGoneException(movieId, e);
        }
    }

    private List<MovieResponse> findAll(String filter) {
        List<MovieResponse> movies = new ArrayList<>();
        int page = 0;
        MoviePageResponse current;
        do {
            int pageNumber = page;
            current = call(() -> movieServiceRestClient.get()
                    .uri(uri -> uri.path("/movies")
                            .queryParam("filter", "{filter}")
                            .queryParam("sort", "{sort}")
                            .queryParam("page", pageNumber)
                            .queryParam("size", PAGE_SIZE)
                            .build(filter, SORT_BY_ID))
                    .retrieve()
                    .body(MoviePageResponse.class));
            if (current == null) {
                throw new DownstreamServiceUnavailableException(UNAVAILABLE, new IllegalStateException("пустой ответ"));
            }
            movies.addAll(current.getItems());
            page++;
        } while (page < current.getTotalPages());
        return movies;
    }

    private static <T> T call(Supplier<T> request) {
        try {
            return request.get();
        } catch (HttpClientErrorException.NotFound e) {
            throw e;
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException("Movie Service отклонил запрос: " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw new DownstreamServiceUnavailableException(UNAVAILABLE, e);
        } catch (RestClientException e) {
            throw new DownstreamServiceUnavailableException(UNAVAILABLE, e);
        }
    }
}
