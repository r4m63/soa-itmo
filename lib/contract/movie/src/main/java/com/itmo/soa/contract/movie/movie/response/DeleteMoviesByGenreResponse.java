package com.itmo.soa.contract.movie.movie.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMoviesByGenreResponse {

    private int deletedCount;
}
