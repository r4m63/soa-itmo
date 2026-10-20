package com.itmo.soa.contract.common;

import java.util.List;

/**
 * Ответ справочников: { "items": [...] }.
 */
public record ItemList<T>(List<T> items) {
}
