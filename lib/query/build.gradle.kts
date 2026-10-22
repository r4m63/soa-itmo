plugins {
    `java-library`
}

dependencies {
    // JpaQueryExecutor — Criteria API; реализацию JPA даёт сервер приложений
    compileOnly(libs.jakarta.persistence.api)
}
