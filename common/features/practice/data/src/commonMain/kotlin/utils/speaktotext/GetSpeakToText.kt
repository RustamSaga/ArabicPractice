package utils.speaktotext

import kotlinx.coroutines.CoroutineScope
import platform.PlatformConfiguration

expect fun createSpeakToText(
    platformConfiguration: PlatformConfiguration,
    scope: CoroutineScope
): SpeakToText