package com.itmo.soa.contract.error.exception;

import com.itmo.soa.contract.error.ErrorType;

import java.util.List;

/**
 * Операция невозможна в текущем состоянии данных.
 */
public class InvalidOperationException extends ServiceException {

    public InvalidOperationException(String message) {
        super(ErrorType.INVALID_OPERATION, message, List.of(), null);
    }
}
