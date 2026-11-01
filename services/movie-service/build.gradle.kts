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

    testCompileOnly(libs.jakartaee.api)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.hibernate.validator)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.postgresql)
    testRuntimeOnly(libs.expressly)
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

tasks.register<Exec>("deploy-movie-service") {
    description = "Собирает WAR и обновляет movie-service на helios"
    group = "deployment"
    val war = tasks.war.get().archiveFile.get().asFile
    dependsOn(tasks.war)
    commandLine(
        "sh", "-c",
        """scp "$1" se:soa/ && echo "cd ~/soa && payara --port 24048 deploy --force=true --name movie-service $2" | ssh se bash -l""",
        "sh", war.path, war.name,
    )
}
