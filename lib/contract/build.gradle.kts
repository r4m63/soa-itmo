plugins {
    `java-library`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.jakarta.validation.api)
    compileOnly(libs.jakarta.ws.rs.api)
}
