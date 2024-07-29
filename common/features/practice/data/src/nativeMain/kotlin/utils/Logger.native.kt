package utils
//import platform.Foundation.NSLog

class IosLogger : Logger {
    override fun log(message: String) {
//        NSLog("KotlinMultiplatform: $message")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }
}

actual fun getPlatformLogger(): Logger = IosLogger()
