plugins {
    `java-library`
}

dependencies {
    // ошибки полей (errors[] в 400) — в формате контракта
    api(project(":lib:contract:common"))
    // JpaQueryExecutor — Criteria API; реализацию JPA даёт сервер приложений
    compileOnly(libs.jakarta.persistence.api)
}
