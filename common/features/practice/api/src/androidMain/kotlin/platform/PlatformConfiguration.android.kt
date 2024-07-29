package platform

import android.content.Context

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface PlatformConfiguration

class AndroidPlatformConfig(
    val context: Context,
    val sourcePath: String,
    val targetPath: String
): PlatformConfiguration {

}