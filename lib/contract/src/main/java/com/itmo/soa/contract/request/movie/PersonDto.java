package com.itmo.soa.contract.request.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {

    @NotBlank(message = "Имя обязательно и не может быть пустым")
    private String name;

    @Size(min = 5, message = "Длина должна быть не меньше 5 символов")
    private String passportID;

    private String hairColor;

    @NotNull(message = "Поле обязательно")
    private String nationality;
}
