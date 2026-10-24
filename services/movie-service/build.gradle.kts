plugins {
    war
}

dependencies {
    compileOnly(libs.jakartaee.api)
    compileOnly(libs.jakarta.data.api)
    compileOnly(libs.jersey.server)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
}
