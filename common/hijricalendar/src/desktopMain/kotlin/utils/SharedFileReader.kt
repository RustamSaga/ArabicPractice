package utils

import java.io.FileNotFoundException

actual class SharedFileReader {
    actual fun loadJsonFile(fileName: String): String {

        val classLoader = Thread.currentThread().contextClassLoader
        return classLoader.getResourceAsStream(fileName)?.bufferedReader().use { it?.readText() }
            ?: throw FileNotFoundException("File not found: $fileName")
    }
}