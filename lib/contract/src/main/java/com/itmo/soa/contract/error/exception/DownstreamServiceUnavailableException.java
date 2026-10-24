package com.itmo.soa.contract.error.exception;

import com.itmo.soa.contract.error.ErrorType;

import java.util.List;

public class DownstreamServiceUnavailableException extends ServiceException {

    public DownstreamServiceUnavailableException(String message, Throwable cause) {
        super(ErrorType.DOWNSTREAM_SERVICE_UNAVAILABLE, message, List.of(), cause);
    }
}
