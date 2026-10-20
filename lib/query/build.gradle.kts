plugins {
    `java-library`
}

dependencies {
    // нужна только адаптеру com.itmo.soa.query.jpa, в рантайме её даёт сервер приложений
    compileOnly(libs.jakarta.persistence.api)
}
