package com.wdbt.query.spring;

import com.wdbt.query.QueryService;
import java.util.List;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

final class QueryWebMvcConfigurer implements WebMvcConfigurer {

    private final QueryService queries;

    QueryWebMvcConfigurer(QueryService queries) {
        this.queries = queries;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new QueryRequestArgumentResolver(queries));
    }
}
