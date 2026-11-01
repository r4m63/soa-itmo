package com.wdbt.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryAttribute {

    /** Публичное имя в HTTP-параметрах. По умолчанию — путь Java-поля. */
    String name() default "";

    /** Путь в persistence backend. По умолчанию — путь Java-поля. */
    String path() default "";

    FieldType type() default FieldType.AUTO;

    /** Пустой массив означает стандартный набор операторов для типа. */
    Operator[] operators() default {};

    boolean sortable() default true;

    boolean nullable() default false;

    /** Поле, по которому OBJECT проверяет наличие или отсутствие значения. */
    String presencePath() default "";
}
