plugins {
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
//    id("com.github.gmazzo.buildconfig")
}


kotlin {
    sourceSets {
        task("testClasses")
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.extensions)
            implementation(libs.kotlin.logging)
            implementation(project(":common:features:practice:api"))
            implementation(project(":common:features:stopwatch_timer:api"))
//            api(project(":common:database:api"))
            api(libs.kodein.di)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":common:features:stopwatch_timer:data"))
        }
        androidMain.dependencies {
            implementation(libs.alphacephei.vosk.android)
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
    namespace = "com.russaga.arabicpractice.common.features.practice.data"
}