package com.itmo.soa.query;

public record QueryError(String parameter, String rejectedValue, String message) {
}
