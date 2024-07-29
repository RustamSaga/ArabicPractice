package utils.speaktotext

import kotlinx.coroutines.CoroutineScope
import mu.KotlinLogging
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import platform.AndroidPlatformConfig
import platform.PlatformConfiguration
import java.io.IOException

private const val SIMPLE_RATE = 16_000f

/**
 * @throws IOException Failed to unpack the model...
 */
actual fun createSpeakToText(
    platformConfiguration: PlatformConfiguration,
    scope: CoroutineScope
): SpeakToText {
    val logger = KotlinLogging.logger {}
    val context = (platformConfiguration as AndroidPlatformConfig).context
    val sourcePath = platformConfiguration.sourcePath
    val targetPath = platformConfiguration.targetPath
    var model: Model = Model()

    StorageService.unpack(context, sourcePath, targetPath,
        { _model ->
            model = _model
        },
        { exception ->
            logger.error(exception) { "Failed to unpack the model" }
            throw IOException("Failed to unpack the model + ${exception.message})")
        }
    )
    val speechService = SpeechService(
        Recognizer(model, SIMPLE_RATE),
        SIMPLE_RATE
    )
    return MicSpeakToText(
        speechService = speechService,
        scope = scope
    )
}