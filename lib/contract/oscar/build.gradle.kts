plugins {
    `java-library`
}

dependencies {
    api(project(":lib:contract:common"))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
