package com.itmo.soa.oscar.infrastructure.integration.movie;

import com.itmo.soa.common.exception.DownstreamServiceUnavailableException;
import com.itmo.soa.common.query.Direction;
import com.itmo.soa.common.query.Filter;
import com.itmo.soa.common.query.Operator;
import com.itmo.soa.common.query.Sort;
import com.itmo.soa.contract.common.dictionary.DictionaryResponse;
import com.itmo.soa.contract.movie.movie.response.MoviePageResponse;
import com.itmo.soa.contract.movie.movie.response.MovieResponse;
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

/**
 * HTTP-клиент Movie Service. Использует только GET /movies (с фильтром) и PATCH /movies/{id}.
 */
@Component
public class MovieServiceClient {

    private static final int PAGE_SIZE = 100;
    /** Сообщение клиенту; подробности — в причине исключения, она пишется в лог. */
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

    /** Справочник жанров Movie Service: список жанров принадлежит ему. */
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

    /**
     * @throws MovieGoneException если фильма уже нет
     */
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

    /** Проходит все страницы: size у Movie Service не больше 100. */
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

    /**
     * Нет соединения, таймаут, 5xx или нечитаемый ответ — сервис недоступен.
     * 4xx — ошибка в нашем запросе: пробрасывается как есть (404 обрабатывает вызывающий код).
     */
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
