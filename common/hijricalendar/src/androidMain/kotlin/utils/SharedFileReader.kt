package utils

import android.content.Context
import android.content.res.AssetManager
import com.russaga.arabicpractice.common.hijricalendar.R


actual class SharedFileReader(private val context: Context) {
    actual fun loadJsonFile(fileName: String): String {
        val inputStream = context.resources.openRawResource(R.raw.hijrichronology)
        return inputStream.bufferedReader().use { it.readText() }
    }
}