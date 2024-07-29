package practice.fake

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import utils.Logger
import utils.speaktotext.SpeakToText

class FakeMicSpeakToText(
    private val text: List<String>,
    private val logger: Logger = FakeLogger()
) : SpeakToText {
    private var scope: CoroutineScope? = null

    private var running = false

    private var startIndex = 0
    override val isActive: Boolean
        get() = scope?.isActive ?: false

    private val _formattedWords: MutableSharedFlow<String> = MutableSharedFlow(0, 50)
    override val formattedWords: SharedFlow<String>
        get() =  _formattedWords


    private val _lastWord: MutableSharedFlow<String> =
        MutableSharedFlow(0, 50)

    override suspend fun startListening() {
        if (isActive) return
        scope?.cancel()
        scope = CoroutineScope(Dispatchers.IO)
        running = true
        listening()
        logger.log("FakeMicSpeakToText. StartListening. running is $running. lastWordIndex = $startIndex")
//        result(_lastWord)
        scope?.launch {
            _lastWord.collect { word ->
                _formattedWords.emit(word)
            }
        }
    }

    override fun resume() {
        scope?.launch {
            startListening()
        }
    }

    private fun listening() {
        scope?.launch {
            while (startIndex <= text.lastIndex && running) {
                delay(500)
                _lastWord.tryEmit(text[startIndex])
                startIndex++
            }
        }
    }

    override suspend fun stopListening() {
        running = false
        scope?.cancel()
        scope = null
    }
}