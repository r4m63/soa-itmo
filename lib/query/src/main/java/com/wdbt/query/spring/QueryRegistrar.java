package com.wdbt.query.spring;

import com.wdbt.query.QueryRegistry;
import com.wdbt.query.QueryService;
import java.util.Map;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

final class QueryRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String REGISTRY_BEAN = QueryRegistrar.class.getPackageName() + ".queryRegistry";
    private static final String SERVICE_BEAN = QueryRegistrar.class.getPackageName() + ".queryService";
    private static final String MVC_BEAN = QueryRegistrar.class.getPackageName() + ".queryWebMvcConfigurer";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableQuery.class.getName());
        Class<?>[] entities = (Class<?>[]) attributes.get("entities");

        registry.registerBeanDefinition(REGISTRY_BEAN, BeanDefinitionBuilder
                .genericBeanDefinition(QueryRegistry.class, () -> QueryRegistry.of(entities))
                .getBeanDefinition());
        registry.registerBeanDefinition(SERVICE_BEAN, BeanDefinitionBuilder
                .genericBeanDefinition(QueryService.class)
                .addConstructorArgReference(REGISTRY_BEAN)
                .getBeanDefinition());
        registry.registerBeanDefinition(MVC_BEAN, BeanDefinitionBuilder
                .genericBeanDefinition(QueryWebMvcConfigurer.class)
                .addConstructorArgReference(SERVICE_BEAN)
                .getBeanDefinition());
    }
}
