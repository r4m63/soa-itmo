package com.itmo.soa.movie.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;

@ApplicationScoped
public class ClockProducer {

    @Produces
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
