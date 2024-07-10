plugins {
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
    id("com.android.library")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.junit)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.adhan2)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.runner)
            implementation(libs.androidx.test.junit)
            implementation(libs.androidx.appcompat)
            implementation(libs.core.ktx)

        }
//        nativeMain.dependencies {
//            implementation(libs.kotlinx.serialization.json)
//        }
//        jsMain.dependencies {
//            implementation(libs.kotlinx.serialization.json)
//        }
    }
}
android {
    namespace = "com.russaga.arabicpractice.common.hijricalendar"
    defaultConfig {
        testInstrumentationRunner =  "androidx.test.runner.AndroidJUnitRunner"
    }
//    defaultConfig {
//        minSdk = libs.versions.android.minSdk.get().toInt()
//        testOptions.targetSdk = libs.versions.android.targetSdk.get().toInt()
//    }
}
