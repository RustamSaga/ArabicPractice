package utils.speaktotext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechStreamService
import java.io.IOException

actual class AudioSpeakToText : AudioToText, RecognitionListener {
    override val lastWord: SharedFlow<String>
        get() = TODO("Not yet implemented")

    private val _lastWord: MutableSharedFlow<String> =
        MutableSharedFlow(REPLAY, EXTRA_BUFFER_CAPACITY)
    private var _isRecognizeFileRun = false
    val isRecognizeFileRun: Boolean get() = _isRecognizeFileRun
    private val logger = KotlinLogging.logger {}
    private var speechStreamService: SpeechStreamService? = null

    override suspend fun recognizeFile(
        stream: PlatformInputStream,
        result: suspend (result: SharedFlow<String>) -> Unit
    ) {

        val inputStream = (stream as PlatformInputStreamImpl).inputStream
        if (_isRecognizeFileRun) return
        _isRecognizeFileRun = true
        try {
//            val recognizer = Recognizer(model, common.SIMPLE_RATE)
//
//            // Ensure to skip 44 bytes from the InputStream
//            val skipped = withContext(Dispatchers.IO) {
//                inputStream.skip(44)
//            }
//            if (skipped != 44L) throw IOException("File too short")
//
//            // Use the input stream safely
//            speechStreamService = SpeechStreamService(recognizer, inputStream, SIMPLE_RATE).apply {
//                start(this@AudioSpeakToText)
//            }
//            result(_lastWord)

        } catch (e: IOException) {
            logger.error { "File error: ${e.message}" }
            throw e
        } finally {
            // Ensure the input stream is closed properly
            try {
                withContext(Dispatchers.IO) {
//                    stream.close()
                }
            } catch (e: IOException) {
                logger.warn { "Failed to close input stream: ${e.message}" }
            }

            // Reset the running flag
            _isRecognizeFileRun = false
        }
    }


    override fun onPartialResult(hypothesis: String?) {
        TODO("Not yet implemented")
    }

    override fun onResult(hypothesis: String?) {
        TODO("Not yet implemented")
    }

    override fun onFinalResult(hypothesis: String?) {
        TODO("Not yet implemented")
    }

    override fun onError(exception: Exception?) {
        TODO("Not yet implemented")
    }

    override fun onTimeout() {
        TODO("Not yet implemented")
    }

}