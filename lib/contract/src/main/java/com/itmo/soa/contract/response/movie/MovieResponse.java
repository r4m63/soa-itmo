package com.itmo.soa.contract.response.movie;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.PersonDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {
    private Integer id;
    private String name;
    private CoordinatesDto coordinates;
    private LocalDate creationDate;
    private Integer oscarsCount;
    private String genre;
    private String mpaaRating;
    private PersonDto screenwriter;
}
