package com.itmo.soa.contract.movie;

import java.util.List;

public record MoviePage(List<Movie> items, int page, int size, long totalElements, int totalPages) {
}
