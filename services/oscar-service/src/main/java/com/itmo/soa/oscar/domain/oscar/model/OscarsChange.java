package com.itmo.soa.oscar.domain.oscar.model;

/**
 * Изменение числа наград фильма. from нужно для отката.
 */
public record OscarsChange(int movieId, int from, int to) {
}
