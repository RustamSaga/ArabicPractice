package utils.speaktotext

import kotlinx.coroutines.flow.SharedFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioSpeakToText : AudioToText {
    override val lastWord: SharedFlow<String>
        get() = TODO("Not yet implemented")

    override suspend fun recognizeFile(
        stream: PlatformInputStream,
        result: suspend (word: SharedFlow<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}