import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties


plugins {
    id("multiplatform-setup")
    id(libs.plugins.sqldelight.get().pluginId)
    id(libs.plugins.serialization.get().pluginId)
    id("com.github.gmazzo.buildconfig")
}
// Tasks desktop
val taskOfCopyDbToDesktopMainRes = "taskOfCopyDatabaseFileFromDer"
val taskOfCopyTestDbToDesktopTestRes = "taskOfCopyTestDatabaseFileFromDer"
// Tasks android
val taskOfCopyDbToAndroidMainAssets = "copyDatabaseToAndroidMainAssets"
val taskOfCopyTestDbToAndroidTestAssets = "copyDatabaseToAndroidTestAssets"

val properties = gradleLocalProperties(projectDir)
val testDatabaseFile: File = file(properties.getProperty("copyTestDatabasePath"))
val releaseDatabaseFile: File = file(properties.getProperty("releaseDatabasePath"))

val databaseName = "AppDatabase"
val databaseFileName = "AppDatabase.db"
val testDatabaseFileName = "TestDatabase.db"

// desktop destination
val desktopDestinationDbPath = "src/desktopMain/resources"
val desktopTestDestinationDbPath = "src/desktopTest/resources"

// android destination
val androidDestinationDbPath = "src/androidMain/assets"
val androidTestDestinationDbPath = "src/androidInstrumentedTest/assets"



tasks.register(taskOfCopyDbToAndroidMainAssets) {
    doLast {
        val resourcesDir = releaseDatabaseFile
        val assetsDir = file(androidDestinationDbPath)
        resourcesDir.copyTo(File(assetsDir, resourcesDir.name), overwrite = true)
    }
}
tasks.register(taskOfCopyTestDbToAndroidTestAssets) {
    doLast {
        val resourcesDir = testDatabaseFile
        val assetsDir = file(androidTestDestinationDbPath)
        resourcesDir.copyTo(File(assetsDir, resourcesDir.name), overwrite = true)
    }
}

tasks.register<Copy>(taskOfCopyDbToDesktopMainRes) {
    val resourcesDir = releaseDatabaseFile
    val destDir = file(desktopDestinationDbPath)

    from(resourcesDir.path) {
        include(resourcesDir.name)
        rename {
            databaseFileName
        }
    }
    into(destDir)
}
tasks.register<Copy>(taskOfCopyTestDbToDesktopTestRes) {
    val resourcesDir = testDatabaseFile
    val destDir = file(desktopTestDestinationDbPath)

    from(resourcesDir.path) {
        include(resourcesDir.name)
        rename {
            testDatabaseFileName
        }
    }
    into(destDir)
}


buildConfig {

    sourceSets {
        named("desktopTest") {
            buildConfigField("testDatabasePath", "$desktopTestDestinationDbPath/")
            buildConfigField("testDatabaseName", testDatabaseFileName)
        }
        named("androidMain") {
            buildConfigField("databaseName", databaseFileName)
            buildConfigField("assetsDbName", releaseDatabaseFile.name)
        }
    }
}


kotlin {
    sourceSets {
        task("testClasses")
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.extensions)
            api(project(":common:database:api"))
            api(libs.kodein.di)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        androidUnitTest.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.kotlin.test.junit)
        }
        androidInstrumentedTest {
            dependencies {
                tasks.preBuild {
                    dependsOn(taskOfCopyTestDbToAndroidTestAssets)
                }
                implementation(libs.sqldelight.android.driver)
                implementation(libs.sqldelight.sqlite.driver)
                implementation(libs.kotlin.test.junit)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.runner)
            }
        }

        desktopMain.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            tasks.desktopProcessResources {
                dependsOn(taskOfCopyDbToDesktopMainRes)
                duplicatesStrategy = DuplicatesStrategy.INCLUDE // Устанавливаем стратегию OVERWRITE
            }
        }
        desktopTest.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.kotlin.test.junit)
            implementation(libs.slf4j.api)
            implementation(libs.logback.classic.v1210)
            tasks.desktopTestProcessResources {
                dependsOn(taskOfCopyTestDbToDesktopTestRes)
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
//        jsMain.dependencies {
//            implementation(libs.sqldelight.web.driver)
//            implementation(npm("sql.js", "1.6.2"))
//            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.1"))
//            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
//        }
    }
}
sqldelight {
    databases {
        create(databaseName) { // write your database name
            packageName = "com.russaga.arabicpractice"// Your app package name
            generateAsync = true
            deriveSchemaFromMigrations = true
            verifyMigrations = true
        }
    }
    linkSqlite = true
}


android {
    namespace = "com.russaga.arabicpractice.common.database"
    defaultConfig {
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner" // junit.framework.AssertionFailedError: No tests found in DatabaseTest
    }
    afterEvaluate {
        tasks.preBuild {
            dependsOn(taskOfCopyDbToAndroidMainAssets)
        }
    }
    buildTypes {
        debug {

        }
        release {

        }
    }

}