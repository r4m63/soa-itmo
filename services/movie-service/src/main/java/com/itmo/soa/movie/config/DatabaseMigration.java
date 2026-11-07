package com.itmo.soa.movie.config;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@ApplicationScoped
public class DatabaseMigration {

    @Resource(lookup = "jdbc/movies")
    DataSource dataSource;

    void migrate(@Observes @Initialized(ApplicationScoped.class) Object event) {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }
}
