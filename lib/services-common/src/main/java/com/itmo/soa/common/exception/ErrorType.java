package com.itmo.soa.common.exception;

/**
 * Реестр видов ошибок сервисов и их HTTP-статусов. Каждый вид — своё исключение-наследник {@link ServiceException};
 * мапперы сервисов берут статус отсюда, поэтому соответствие «ошибка → HTTP» задано в одном месте.
 */
public enum ErrorType {

    /** Неверные параметры или тело запроса: ответ ValidationError со списком errors. */
    INVALID_DATA(400),

    /** Запрошенного ресурса нет. */
    NOT_FOUND(404),

    /** Запрос корректен, но операция невозможна в текущем состоянии данных. */
    INVALID_OPERATION(409),

    /** Сервис, от которого зависит операция, недоступен. */
    DOWNSTREAM_SERVICE_UNAVAILABLE(503);

    private final int httpStatus;

    ErrorType(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
