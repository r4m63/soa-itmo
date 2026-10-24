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
