package com.itmo.soa.contract.response.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoviePageResponse {

    private List<MovieResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
