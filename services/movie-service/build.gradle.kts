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

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.postgresql)
}

tasks.test {
    useJUnitPlatform {
        excludeTags("seeder")
    }
}

tasks.register<Test>("seed") {
    description = "Заполняет запущенный movie-service фильмами из src/test/resources/movies.csv"
    group = "application"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform {
        includeTags("seeder")
    }
    outputs.upToDateWhen { false }
    testLogging {
        showStandardStreams = true
    }
}
