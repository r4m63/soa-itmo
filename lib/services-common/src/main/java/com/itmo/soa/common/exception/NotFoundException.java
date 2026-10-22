package com.itmo.soa.common.exception;

import java.util.List;

/**
 * Запрошенного ресурса нет.
 */
public class NotFoundException extends ServiceException {

    public NotFoundException(String message) {
        super(ErrorType.NOT_FOUND, message, List.of(), null);
    }
}
