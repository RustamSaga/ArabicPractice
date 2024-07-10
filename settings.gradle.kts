rootProject.name = "ArabicPractice"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

//include(":composeApp")
include(":common:core")
include(":common:core_compose")
include(":common:core_utils")
include(":common:database:api")
include(":common:database:data")
include(":common:features:auth:api")
include(":common:features:auth:compose")
include(":common:features:auth:data")
include(":common:features:auth:presentation")
include(":common:features:onboarding:api")
include(":common:features:onboarding:data")
include(":common:features:onboarding:presentation")
include(":common:features:practice:api")
include(":common:features:practice:data")
include(":common:features:practice:presentation")
include(":common:features:roadmap:api")
include(":common:features:roadmap:data")
include(":common:features:roadmap:presentation")
include(":common:features:target:api")
include(":common:features:target:data")
include(":common:features:target:presentation")
include(":common:features:theory:morphology")
include(":common:features:theory:phonetic")
include(":common:features:theory:rhetoric")
include(":common:features:theory:syntax")
include(":common:hijricalendar")
include(":common:languageconfig:api")
include(":common:languageconfig:data")
include(":roadmap_lib")
//include(":multiplatformBuildConfig")
//include(":server")
//include(":shared")