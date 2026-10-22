package com.itmo.soa.contract.response.oscar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedistributeOscarsResponse {

    private int sourceMovies;
    private int targetMovies;
    private long transferredOscars;
}
