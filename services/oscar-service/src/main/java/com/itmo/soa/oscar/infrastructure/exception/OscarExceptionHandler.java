package com.itmo.soa.oscar.infrastructure.exception;

import com.itmo.soa.common.exception.ErrorType;
import com.itmo.soa.common.exception.ServiceException;
import com.itmo.soa.contract.common.error.ErrorResponse;
import com.itmo.soa.contract.common.error.ValidationErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Ошибки в формате спецификации: бизнес-ошибки по реестру ErrorType, ошибки Spring MVC со своим статусом, остальное — 500.
 */
@RestControllerAdvice
public class OscarExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(OscarExceptionHandler.class);

    /** Все бизнес-ошибки: HTTP-статус из реестра ErrorType, тело — Error или ValidationError (если есть errors). */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> service(ServiceException e) {
        HttpStatusCode status = HttpStatusCode.valueOf(e.type().httpStatus());
        if (e.type() == ErrorType.DOWNSTREAM_SERVICE_UNAVAILABLE) {
            log.warn(e.getMessage(), e);
        }
        if (!e.errors().isEmpty()) {
            return ResponseEntity.status(status).body(new ValidationErrorResponse(e.getMessage(), now(), e.errors()));
        }
        return error(status, e.getMessage());
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
        return error(HttpStatusCode.valueOf(500), "Внутренняя ошибка сервера");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, now()));
    }

    private Instant now() {
        return Instant.now();
    }
}
