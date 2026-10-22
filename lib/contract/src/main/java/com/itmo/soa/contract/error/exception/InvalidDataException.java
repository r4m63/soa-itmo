package com.itmo.soa.contract.error.exception;

import com.itmo.soa.contract.error.ErrorType;
import com.itmo.soa.contract.error.response.FieldError;

import java.util.List;

/**
 * Неверные параметры или тело запроса; errors — все нарушения сразу.
 */
public class InvalidDataException extends ServiceException {

    public InvalidDataException(String message, List<FieldError> errors) {
        super(ErrorType.INVALID_DATA, message, errors, null);
    }
}
