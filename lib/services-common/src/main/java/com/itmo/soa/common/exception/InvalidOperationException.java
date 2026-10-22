package com.itmo.soa.common.exception;

import java.util.List;

/**
 * Операция невозможна в текущем состоянии данных.
 */
public class InvalidOperationException extends ServiceException {

    public InvalidOperationException(String message) {
        super(ErrorType.INVALID_OPERATION, message, List.of(), null);
    }
}
