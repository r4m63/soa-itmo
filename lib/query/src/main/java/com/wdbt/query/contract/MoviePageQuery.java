package com.wdbt.query.contract;

import java.util.List;

public record MoviePageQuery(
        List<String> filters,
        List<String> sorts,
        String page,
        String size
) {
}
