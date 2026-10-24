package com.itmo.soa.contract.response.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryResponse<T> {
    private List<T> items;
}
