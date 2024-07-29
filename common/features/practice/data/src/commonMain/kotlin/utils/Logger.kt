package utils

interface Logger {
    fun log(message: String)
    fun error(message: String)
}

expect fun getPlatformLogger(): Logger