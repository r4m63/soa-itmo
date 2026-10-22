plugins {
    war
}

dependencies {
    compileOnly(libs.jakartaee.api)
    // Jakarta Data не входит в jakartaee-api 11.0.0; реализацию даёт Payara
    compileOnly(libs.jakarta.data.api)
    // Jersey — реализация JAX-RS в Payara: ParamException говорит, какой параметр пути не разобрался
    compileOnly(libs.jersey.server)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
}
