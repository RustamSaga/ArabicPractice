plugins {
    id("multiplatform-setup")
    id(libs.plugins.sqldelight.get().pluginId)
    id(libs.plugins.serialization.get().pluginId)
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.extensions)
            implementation(libs.kotlinx.serialization.core)

            api(libs.kodein.di)
        }
        androidMain.dependencies {
//            implementation(libs.sqldelight.android.driver)
        }
        nativeMain.dependencies {
//            implementation(libs.sqldelight.native.driver)
        }
        desktopMain.dependencies {
//            implementation(libs.sqldelight.sqlite.driver)
        }
        jsMain.dependencies {
//            implementation(libs.sqldelight.web.driver)
//            implementation(npm("sql.js", "1.6.2"))
//            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.1"))
//            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }
    }
}
//sqldelight {
//    databases {
//        create("ArabicPracticeDatabase") { // write your database name
//            packageName.set("com.russaga.arabicpractice")// Your app package name
//            generateAsync.set(true)
//            this.schemaOutputDirectory = file("src.commonMain.sqldelight.schema.ArabicPracticeDatabase.db")
//        }
//    }
//    linkSqlite = true
//}

android {
    namespace = "com.russaga.arabicpractice.common.database"
}