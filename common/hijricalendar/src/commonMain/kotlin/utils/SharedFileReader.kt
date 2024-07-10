package utils

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class SharedFileReader {
    fun loadJsonFile(fileName: String): String
}