plugins {
    war
}

dependencies {
    compileOnly(libs.jakartaee.api)
    // Jersey — реализация JAX-RS в Payara: ParamException говорит, какой параметр пути не разобрался
    compileOnly(libs.jersey.server)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
}
