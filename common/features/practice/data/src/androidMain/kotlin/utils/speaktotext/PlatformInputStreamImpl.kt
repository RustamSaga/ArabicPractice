package utils.speaktotext

import java.io.InputStream

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformInputStreamImpl(
    val inputStream: InputStream
): PlatformInputStream