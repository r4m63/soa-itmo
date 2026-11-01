package com.wdbt.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryEntity {

    int defaultPageSize() default 20;

    int maxPageSize() default 100;

    /** Формат: {@code field;asc} или {@code field;desc}. */
    String[] defaultSort() default {};

    String tieBreaker() default "";

    Direction tieBreakerDirection() default Direction.ASC;
}
