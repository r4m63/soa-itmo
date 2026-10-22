package com.itmo.soa.contract.oscar.oscar.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Параметры пути /oscar/genres/redistribute-rewards/{from-genre}/{to-genre}.
 * Жанры — строки: их список принадлежит Movie Service, Oscar Service сверяется с его справочником.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedistributeOscarsPathParams {

    private String fromGenre;
    private String toGenre;
}
