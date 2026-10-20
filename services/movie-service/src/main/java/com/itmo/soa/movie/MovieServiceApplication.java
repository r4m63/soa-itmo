package com.itmo.soa.movie;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Ресурсы и провайдеры находятся сканированием. Context root — "/", пути как в спецификации: /movies/...
 */
@ApplicationPath("/")
public class MovieServiceApplication extends Application {
}
