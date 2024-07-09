plugins {
    id("multiplatform-setup")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
android {
    namespace = "com.russaga.arabicpractice.common.languageconfig.data"
}