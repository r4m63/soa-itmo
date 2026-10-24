package com.itmo.soa.contract.request.oscar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedistributeOscarsPathParams {

    private String fromGenre;
    private String toGenre;
}
