package com.wdbt.query.jaxrs;

import jakarta.ws.rs.QueryParam;
import java.util.List;

public class QueryParameters {

    @QueryParam("filter")
    private List<String> filters;

    @QueryParam("sort")
    private List<String> sorts;

    @QueryParam("page")
    private String page;

    @QueryParam("size")
    private String size;

    public List<String> filters() {
        return filters;
    }

    public List<String> sorts() {
        return sorts;
    }

    public String page() {
        return page;
    }

    public String size() {
        return size;
    }
}
