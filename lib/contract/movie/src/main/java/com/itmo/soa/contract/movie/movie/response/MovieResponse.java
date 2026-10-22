package com.itmo.soa.contract.movie.movie.response;

import com.itmo.soa.contract.movie.movie.dto.CoordinatesDto;
import com.itmo.soa.contract.movie.movie.dto.PersonDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
