package utils.speaktotext

import kotlinx.coroutines.flow.SharedFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MicSpeakToText : SpeakToText {
    override val isActive: Boolean
        get() = TODO("Not yet implemented")
    override val formattedWords: SharedFlow<String>
        get() = TODO("Not yet implemented")

    override suspend fun startListening() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }
//    override val lastWord: SharedFlow<String>
//        get() = TODO("Not yet implemented")



    override suspend fun stopListening() {
        TODO("Not yet implemented")
    }
}