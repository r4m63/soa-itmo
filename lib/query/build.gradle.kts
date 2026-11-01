plugins {
    `java-library`
}

dependencies {
    compileOnly(libs.lombok)
    compileOnly(libs.jakarta.persistence.api)
    compileOnly(libs.jakarta.ws.rs.api)
    compileOnly(libs.spring.webmvc)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jakartaee.api)
    testImplementation(libs.spring.webmvc)
    testImplementation(libs.spring.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}
