plugins {
    war
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
    providedRuntime("com.fasterxml.jackson.core:jackson-annotations")
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
}

tasks.bootWar {
    enabled = false
}

tasks.war {
    archiveClassifier = ""
}

tasks.register<Exec>("deploy-oscar-service") {
    description = "Собирает WAR и обновляет oscar-service на helios"
    group = "deployment"
    val war = tasks.war.get().archiveFile.get().asFile
    dependsOn(tasks.war)
    commandLine(
        "sh", "-c",
        """scp "$1" se: && echo "cd ~ && payara --port 24148 deploy --force=true --name oscar-service $2 && rm -- $2" | ssh se bash -l""",
        "sh", war.path, war.name,
    )
}
