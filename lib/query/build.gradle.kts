plugins {
    `java-library`
}

dependencies {
    compileOnly(libs.lombok)
    compileOnly(libs.jakarta.persistence.api)
    compileOnly(libs.jakarta.ws.rs.api)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
