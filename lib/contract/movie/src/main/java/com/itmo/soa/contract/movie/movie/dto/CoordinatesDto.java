package com.itmo.soa.contract.movie.movie.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDto {

    @NotNull(message = "Поле обязательно")
    private Integer x;

    @NotNull(message = "Поле обязательно")
    private Long y;
}
