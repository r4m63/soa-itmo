package com.wdbt.query.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wdbt.query.Direction;
import com.wdbt.query.Query;
import com.wdbt.query.QueryAttribute;
import com.wdbt.query.QueryEntity;
import com.wdbt.query.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class QueryRequestArgumentResolverTest {

    @Test
    void enableQueryRegistersBeansAndResolvesControllerArgument() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(QueryConfiguration.class)) {
            QueryService queries = context.getBean(QueryService.class);
            assertNotNull(queries);

            QueryRequestArgumentResolver resolver = new QueryRequestArgumentResolver(queries);
            MethodParameter parameter = new MethodParameter(
                    SampleController.class.getDeclaredMethod("list", Query.class), 0);
            assertTrue(resolver.supportsParameter(parameter));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("filter", "name;contains;John");
            request.addParameter("sort", "name;desc");
            request.addParameter("page", "2");
            request.addParameter("size", "25");

            Query query = (Query) resolver.resolveArgument(parameter, null, new ServletWebRequest(request), null);
            assertEquals("John", query.filters().getFirst().value());
            assertEquals("name", query.effectiveSort().getFirst().field().name());
            assertEquals(Direction.DESC, query.effectiveSort().getFirst().direction());
            assertEquals(2, query.page().page());
            assertEquals(25, query.page().size());
        }
    }

    @Configuration
    @EnableQuery(entities = SampleEntity.class)
    static class QueryConfiguration {
    }

    static class SampleController {
        void list(@QueryRequest(SampleEntity.class) Query query) {
        }
    }

    @QueryEntity
    static class SampleEntity {
        @QueryAttribute
        private String name;
    }
}
