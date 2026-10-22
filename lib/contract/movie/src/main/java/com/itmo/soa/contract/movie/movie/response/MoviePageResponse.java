package com.itmo.soa.contract.movie.movie.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoviePageResponse {

    private List<MovieResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
