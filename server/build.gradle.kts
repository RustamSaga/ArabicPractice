plugins {
    id(libs.plugins.kotlinJvm.get().pluginId)
    id(libs.plugins.ktor.get().pluginId)
    application
}

group = "com.russaga.arabicpractice"
version = "1.0.0"
application {
    mainClass.set("com.russaga.arabicpractice.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}