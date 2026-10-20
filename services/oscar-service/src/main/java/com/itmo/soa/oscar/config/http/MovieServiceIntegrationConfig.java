package com.itmo.soa.oscar.config.http;

import com.itmo.soa.oscar.config.properties.MovieServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class MovieServiceIntegrationConfig {

    /**
     * HTTPS-клиент к Movie Service. Сертификат Payara №1 должен лежать в truststore этой JVM
     * (cacerts.jks домена Payara №2) — тогда системный SSLContext ему доверяет.
     */
    @Bean
    public RestClient movieServiceRestClient(MovieServiceProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
