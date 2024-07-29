package utils.speaktotext

import kotlinx.coroutines.flow.SharedFlow

interface SpeakToText {
//    val lastWord: SharedFlow<String>
    val isActive: Boolean
    val formattedWords: SharedFlow<String>

    suspend fun startListening()
    fun resume()
    suspend fun stopListening()
}

interface AudioToText {
    val lastWord: SharedFlow<String>
    suspend fun recognizeFile(
        stream: PlatformInputStream,
        result: suspend (word: SharedFlow<String>) -> Unit
    )

}