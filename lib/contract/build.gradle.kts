plugins {
    `java-library`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    // аннотации валидации — часть контракта; проверяет их сервис, который принимает запрос
    compileOnly(libs.jakarta.validation.api)
    // @PathParam в DTO параметров пути Movie Service (JAX-RS)
    compileOnly(libs.jakarta.ws.rs.api)
}
