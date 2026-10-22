package com.itmo.soa.contract.common.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Ответ справочников: { "items": [...] }.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryResponse<T> {

    private List<T> items;
}
