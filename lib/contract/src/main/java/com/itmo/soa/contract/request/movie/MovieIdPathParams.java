package com.itmo.soa.contract.request.movie;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.PathParam;
import lombok.Getter;

/**
 * Параметр пути {id}: положительное int32.
 */
@Getter
public class MovieIdPathParams {

    @PathParam("id")
    @Positive(message = "id должен быть положительным целым числом")
    private int id;
}
