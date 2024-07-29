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
            implementation(project(":common:features:stopwatch_timer:api"))
//            api(libs.kodein.di)
        }
        androidMain.dependencies {
//            implementation(libs.vosk.android)
        }
        desktopMain.dependencies {
//            implementation(libs.vosk.generic)

        }
    }
}

android {
    namespace = "com.russaga.arabicpractice.common.features.practice.api"
}
