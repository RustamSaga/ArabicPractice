package com.russaga.arabicpractice.platform

import ArabicPractice.common.database.data.AndroidMainBuildConfig
import android.content.Context

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformConfiguration (
    val androidContext: Context,
    val databaseName: String,
    val assetsDatabaseName: String
) {
    companion object {
        fun build(context: Context): PlatformConfiguration {
            return PlatformConfiguration(
                androidContext = context,
                databaseName = AndroidMainBuildConfig.databaseName,
                assetsDatabaseName = AndroidMainBuildConfig.assetsDbName

            )
        }
    }
}