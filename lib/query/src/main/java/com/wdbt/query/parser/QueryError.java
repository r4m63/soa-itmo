package com.wdbt.query.parser;

public record QueryError(String parameter, String rejectedValue, String message) {
}
