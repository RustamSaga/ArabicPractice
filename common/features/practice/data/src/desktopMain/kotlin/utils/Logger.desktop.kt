package utils

import org.slf4j.LoggerFactory

class DesktopLogger : Logger {
    val TAG = "KotlinMultiplatform"
    private val logger = LoggerFactory.getLogger(TAG)
    override fun log(message: String) {
        logger.info(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }
}

actual fun getPlatformLogger(): Logger {
    return DesktopLogger()
}