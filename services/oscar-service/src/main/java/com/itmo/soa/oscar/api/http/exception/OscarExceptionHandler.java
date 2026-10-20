package com.itmo.soa.oscar.api.http.exception;

import com.itmo.soa.contract.error.ErrorResponse;
import com.itmo.soa.contract.error.ValidationErrorResponse;
import com.itmo.soa.oscar.domain.oscar.model.InvalidGenreException;
import com.itmo.soa.oscar.domain.oscar.model.NoTargetMoviesException;
import com.itmo.soa.oscar.infrastructure.integration.movie.MovieServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.Instant;

/**
 * Ошибки в формате спецификации: 400, 409, 503, 500.
 */
@RestControllerAdvice
public class OscarExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(OscarExceptionHandler.class);

    private final Clock clock;

    public OscarExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(InvalidGenreException.class)
    public ResponseEntity<ValidationErrorResponse> invalidGenre(InvalidGenreException e) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(e.getMessage(), now(), e.errors()));
    }

    @ExceptionHandler(NoTargetMoviesException.class)
    public ResponseEntity<ErrorResponse> noTargetMovies(NoTargetMoviesException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MovieServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> movieServiceUnavailable(MovieServiceUnavailableException e) {
        log.warn("Movie Service недоступен", e);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Movie Service недоступен");
    }

    /** Ошибки самого Spring MVC (нет пути, не тот метод) сохраняют свой статус, остальное — 500. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandled(Exception e) {
        if (e instanceof org.springframework.web.ErrorResponse springError) {
            HttpStatusCode status = springError.getStatusCode();
            String message = switch (status.value()) {
                case 404 -> "Ресурс не найден";
                case 405 -> "Метод не поддерживается";
                case 406 -> "Ответ доступен только в application/json";
                default -> e.getMessage();
            };
            return error(status, message);
        }
        log.error("Необработанная ошибка", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, now()));
    }

    private Instant now() {
        return Instant.now(clock);
    }
}
