package com.wdbt.query;

@FunctionalInterface
public interface QueryExecutor<T> {

    Page<T> execute(Query query);
}
