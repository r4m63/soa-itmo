plugins {
    `java-library`
}

dependencies {
    api(project(":lib:contract:common"))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.jakarta.validation.api)
    // @PathParam в DTO параметров пути: movie-service на JAX-RS
    compileOnly(libs.jakarta.ws.rs.api)
}
