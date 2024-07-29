package utils.speaktotext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import utils.Logger
import utils.getPlatformLogger

internal const val REPLAY = 0
internal const val EXTRA_BUFFER_CAPACITY = 50
private const val EMPTY_STRING = ""

/**
 * Implementation of the [SpeakToText] interface for handling speech-to-text functionality using a [SpeechService].
 * This class is responsible for starting and stopping the listening process, processing partial and final recognition results,
 * and logging relevant information during the speech-to-text conversion.
 *
 * @param scope The [CoroutineScope] for launching coroutines.
 * @param speechService The [SpeechService] instance used for speech recognition.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MicSpeakToText(
    private val logger: Logger = getPlatformLogger(),
    private val scope: CoroutineScope,
    private val speechService: SpeechService
) : SpeakToText, RecognitionListener {

    private var job: Job? = null

    private val wordsFromMic: MutableSharedFlow<String> =
        MutableSharedFlow(REPLAY, EXTRA_BUFFER_CAPACITY)

    override val isActive: Boolean get() = job?.isActive ?: false

    private val _formattedWords: MutableSharedFlow<String> = MutableSharedFlow(REPLAY, EXTRA_BUFFER_CAPACITY)
    override val formattedWords: SharedFlow<String>
        get() = _formattedWords

    override suspend fun startListening() {
        if (isActive) return

        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            speechService.startListening(this@MicSpeakToText)
            logger.log("Start listening")
        }
        wordsFromMic.collect {
            val json = JSONObject(it)
            if (json.has("partial")) {
                val partialText = json.getString("partial")
                val mLastWord = extractLastWord(partialText)
                if (mLastWord != EMPTY_STRING) {
                    _formattedWords.emit(mLastWord)
                }
            }
            logger.log("collect and format from Json to single word")
        }

    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override suspend fun stopListening() {
        if (!isActive) return

        speechService.stop()
        job?.cancel()
        job = null
        logger.log("Stop listening")

    }

    override fun onPartialResult(hypothesis: String?) {
        hypothesis?.let {
            wordsFromMic.tryEmit(it)
        }
    }

    override fun onResult(hypothesis: String?) {
        logger.log("onResult: $hypothesis")
    }

    override fun onFinalResult(hypothesis: String?) {
        logger.log("Final result: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        logger.error("$exception: Recognition error")
        job?.cancel()
        job = null
    }

    override fun onTimeout() {
        logger.log("Recognition timeout")
        job?.cancel()
        job = null
    }

    private fun extractLastWord(text: String): String {
        val words = text.split(" ")
        return words.lastOrNull() ?: ""
    }

}