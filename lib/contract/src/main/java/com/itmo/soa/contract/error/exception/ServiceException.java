package com.itmo.soa.contract.error.exception;

import com.itmo.soa.contract.error.ErrorType;
import com.itmo.soa.contract.error.response.FieldError;

import java.util.List;

/**
 * Бизнес-ошибка сервиса. Сообщение уходит клиенту в поле message, errors — только у {@link ErrorType#INVALID_DATA}.
 */
public abstract class ServiceException extends RuntimeException {

    private final ErrorType type;
    private final List<FieldError> errors;

    protected ServiceException(ErrorType type, String message, List<FieldError> errors, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.errors = List.copyOf(errors);
    }

    public ErrorType type() {
        return type;
    }

    public List<FieldError> errors() {
        return errors;
    }
}
