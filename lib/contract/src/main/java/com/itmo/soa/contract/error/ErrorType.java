package com.itmo.soa.contract.error;

import com.itmo.soa.contract.error.exception.ServiceException;

public enum ErrorType {

    INVALID_DATA(400),
    NOT_FOUND(404),
    INVALID_OPERATION(409),
    DOWNSTREAM_SERVICE_UNAVAILABLE(503);

    private final int httpStatus;

    ErrorType(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
