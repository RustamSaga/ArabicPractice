plugins {
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
}

kotlin {
    sourceSets {
        task("testClasses")
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.extensions)
            implementation(libs.kotlin.logging)
            api(libs.kodein.di)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlin.test.junit)
        }
        desktopTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.slf4j.api)
            implementation(libs.logback.classic.v1210)
        }
    }
}

android {
    namespace = "com.russaga.arabicpractice.common.features.stopwatch_timer.api"
}