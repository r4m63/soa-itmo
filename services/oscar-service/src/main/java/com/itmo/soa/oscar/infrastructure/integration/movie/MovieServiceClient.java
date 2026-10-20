package com.itmo.soa.oscar.infrastructure.integration.movie;

import com.itmo.soa.contract.movie.Movie;
import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.contract.movie.MoviePage;
import com.itmo.soa.contract.movie.MpaaRating;
import com.itmo.soa.query.Direction;
import com.itmo.soa.query.Filter;
import com.itmo.soa.query.Operator;
import com.itmo.soa.query.Sort;
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
    private static final String SORT_BY_ID = Sort.format("id", Direction.ASC);

    private final RestClient movieServiceRestClient;

    public MovieServiceClient(RestClient movieServiceRestClient) {
        this.movieServiceRestClient = movieServiceRestClient;
    }

    public List<Movie> findAllByMpaaRating(MpaaRating mpaaRating) {
        return findAll(Filter.format("mpaaRating", Operator.EQ, mpaaRating));
    }

    public List<Movie> findAllByGenre(MovieGenre genre) {
        return findAll(Filter.format("genre", Operator.EQ, genre));
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
    private List<Movie> findAll(String filter) {
        List<Movie> movies = new ArrayList<>();
        int page = 0;
        MoviePage current;
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
                    .body(MoviePage.class));
            if (current == null) {
                throw new MovieServiceUnavailableException("Movie Service вернул пустой ответ", null);
            }
            movies.addAll(current.items());
            page++;
        } while (page < current.totalPages());
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
            throw new MovieServiceUnavailableException("Movie Service ответил " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            throw new MovieServiceUnavailableException("Movie Service недоступен: " + e.getMessage(), e);
        }
    }
}
