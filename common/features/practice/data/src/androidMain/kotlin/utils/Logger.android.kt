package utils

import android.util.Log

class AndroidLogger : Logger {
    val TAG = "KotlinMultiplatform"
    override fun log(message: String) {
        Log.d(TAG, message)
    }

    override fun error(message: String) {
        Log.e("KotlinMultiplatform", message)
    }
}


actual fun getPlatformLogger(): Logger = AndroidLogger()