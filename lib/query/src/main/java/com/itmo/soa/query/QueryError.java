package com.itmo.soa.query;

/**
 * @param parameter параметр запроса: {@code filter[1]}, {@code sort[0]}, {@code page}, {@code size}
 */
public record QueryError(String parameter, String rejectedValue, String message) {
}
