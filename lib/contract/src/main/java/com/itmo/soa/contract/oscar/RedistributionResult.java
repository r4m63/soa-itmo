package com.itmo.soa.contract.oscar;

public record RedistributionResult(int sourceMovies, int targetMovies, long transferredOscars) {
}
