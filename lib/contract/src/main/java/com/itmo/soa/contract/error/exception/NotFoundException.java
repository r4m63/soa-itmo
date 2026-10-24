package com.itmo.soa.contract.error.exception;

import com.itmo.soa.contract.error.ErrorType;

import java.util.List;

public class NotFoundException extends ServiceException {

    public NotFoundException(String message) {
        super(ErrorType.NOT_FOUND, message, List.of(), null);
    }
}
