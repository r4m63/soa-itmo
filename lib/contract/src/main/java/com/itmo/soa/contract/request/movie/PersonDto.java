package com.itmo.soa.contract.request.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDto {

    @NotNull(message = "Поле обязательно")
    @Pattern(regexp = ".*\\S.*", message = "Имя не может быть пустым")
    private String name;

    @Size(min = 5, message = "Длина должна быть не меньше 5 символов")
    private String passportID;

    @Pattern(
            regexp = "RED|BLUE|ORANGE|WHITE",
            message = "Допустимые значения: RED, BLUE, ORANGE, WHITE"
    )
    private String hairColor;

    @NotNull(message = "Поле обязательно")
    @Pattern(
            regexp = "RUSSIA|UNITED_KINGDOM|FRANCE|SPAIN|NORTH_KOREA",
            message = "Допустимые значения: RUSSIA, UNITED_KINGDOM, FRANCE, SPAIN, NORTH_KOREA"
    )
    private String nationality;
}
