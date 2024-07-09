plugins {
    id("multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":common:features:target:api"))
            implementation(project(":common:core"))
        }
    }
}
android {
    namespace = "com.russaga.arabicpractice.features.target.data"
}
