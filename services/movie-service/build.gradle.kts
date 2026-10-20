plugins {
    war
}

dependencies {
    compileOnly(libs.jakartaee.api)
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
}
