package com.itmo.soa.contract.response.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMoviesByGenreResponse {

    private int deletedCount;
}
