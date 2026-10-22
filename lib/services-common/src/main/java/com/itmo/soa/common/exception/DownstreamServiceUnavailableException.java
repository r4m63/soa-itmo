package com.itmo.soa.common.exception;

import java.util.List;

/**
 * Сервис, от которого зависит операция, недоступен: нет соединения, таймаут, 5xx или нечитаемый ответ.
 */
public class DownstreamServiceUnavailableException extends ServiceException {

    public DownstreamServiceUnavailableException(String message, Throwable cause) {
        super(ErrorType.DOWNSTREAM_SERVICE_UNAVAILABLE, message, List.of(), cause);
    }
}
