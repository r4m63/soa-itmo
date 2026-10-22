package com.itmo.soa.contract.request.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {

    @NotNull(message = "Поле обязательно")
    @Pattern(regexp = ".*\\S.*", message = "Имя не может быть пустым")
    private String name;

    @Size(min = 5, message = "Длина должна быть не меньше 5 символов")
    private String passportID;

    private String hairColor;

    @NotNull(message = "Поле обязательно")
    private String nationality;
}
