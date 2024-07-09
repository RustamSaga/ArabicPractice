plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("multiplatform")
}

kotlin {
//    js {
//        browser()
//        binaries.executable()
//    }

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    androidTarget()

    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
        }
    }
}


android {
    compileSdk = 34
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
    defaultConfig {
        minSdk = 24 // libs.versions.android.minSdk.get().toInt()
    }
}