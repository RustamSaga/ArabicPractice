plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    id(libs.plugins.kotlinMultiplatform.get().pluginId) apply false
    id(libs.plugins.androidApplication.get().pluginId) apply false
    id(libs.plugins.jetbrainsCompose.get().pluginId) apply false
    id(libs.plugins.libres.get().pluginId) apply false
    id(libs.plugins.serialization.get().pluginId) apply false
    id("com.github.gmazzo.buildconfig") version "5.3.5" apply false
//    id(libs.plugins.buildConfig.get().pluginId)
}