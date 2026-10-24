package com.itmo.soa.oscar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("movie-service")
public record MovieServiceProperties(
        String baseUrl,
        @DefaultValue("5s") Duration connectTimeout,
        @DefaultValue("30s") Duration readTimeout
) {

    public MovieServiceProperties {
        if (baseUrl == null || !baseUrl.startsWith("https://")) {
            throw new IllegalArgumentException("movie-service.base-url должен начинаться с https://, получено: " + baseUrl);
        }
    }
}
