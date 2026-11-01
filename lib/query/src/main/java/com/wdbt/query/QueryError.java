package com.wdbt.query;

public record QueryError(String parameter, String rejectedValue, String message) {
}
