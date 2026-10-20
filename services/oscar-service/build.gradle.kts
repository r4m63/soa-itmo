plugins {
    war
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    // встроенный Tomcat — только для локального java -jar, в Payara не попадает в WEB-INF/lib
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
}
