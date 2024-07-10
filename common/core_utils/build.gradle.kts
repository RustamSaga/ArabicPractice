plugins {
    id("multiplatform-setup")
    id(libs.plugins.serialization.get().pluginId)
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.core)
            api(libs.kotlinx.coroutines)

            api(libs.ktor.client.core)
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.logging)

            implementation(libs.multiplatform.settings.core)
            implementation(libs.multiplatform.settings.no.arg)

            implementation(libs.kotlinx.datetime)

            api(libs.kodein.di)
        }
        commonTest.dependencies {
//            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlin.test.junit)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
//        jsMain.dependencies {
//            implementation(libs.sqldelight.web.driver)
//            implementation(npm("sql.js", "1.6.2"))
//            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.1"))
//            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
//        }
    }
}

android {
    namespace = "com.russaga.arabicpractice.common.core_utils"
}