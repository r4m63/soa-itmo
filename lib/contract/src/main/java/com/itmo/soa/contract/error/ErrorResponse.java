package com.itmo.soa.contract.error;

import java.time.Instant;

public record ErrorResponse(String message, Instant timestamp) {
}
