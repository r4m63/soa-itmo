plugins {
    war
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    // сервлет-контейнер даёт Payara: встроенный Tomcat не попадает в WEB-INF/lib
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
    // аннотации Jackson берутся из Payara (там 2.22, совместимы с Jackson 3): своя копия в WAR конфликтует
    // с jackson-databind 2 из модулей Payara, который Spring тоже находит, — LinkageError при старте
    providedRuntime("com.fasterxml.jackson.core:jackson-annotations")
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
}

// деплоится обычный WAR в Payara; исполняемый bootWar не нужен и занимал бы то же имя файла
tasks.bootWar {
    enabled = false
}

tasks.war {
    archiveClassifier = ""
}
