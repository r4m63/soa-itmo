package com.itmo.soa.contract.error;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(String message, Instant timestamp, List<FieldError> errors) {
}
