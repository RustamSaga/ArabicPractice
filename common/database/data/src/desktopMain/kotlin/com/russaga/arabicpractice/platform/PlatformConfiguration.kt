package com.russaga.arabicpractice.platform

import ArabicPractice.common.database.data.DesktopMainBuildConfig

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformConfiguration(
    val databasePath: String
) {
    companion object {
        fun build(): PlatformConfiguration {
            return PlatformConfiguration(
                databasePath = DesktopMainBuildConfig.mainDatabasePath
            )
        }
    }
}