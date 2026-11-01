package com.wdbt.query.spring;

import com.wdbt.query.Query;
import com.wdbt.query.QueryService;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

final class QueryRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private final QueryService queries;

    QueryRequestArgumentResolver(QueryService queries) {
        this.queries = queries;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(QueryRequest.class)
                && Query.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        QueryRequest annotation = parameter.getParameterAnnotation(QueryRequest.class);
        return queries.parse(annotation.value(),
                parameters(webRequest, "filter"), parameters(webRequest, "sort"),
                webRequest.getParameter("page"), webRequest.getParameter("size"));
    }

    private static List<String> parameters(NativeWebRequest request, String name) {
        String[] values = request.getParameterValues(name);
        return values == null ? null : Arrays.asList(values);
    }
}
