package utils

import kotlin.math.abs

internal const val START_FORMATTED_TIME = "00:00:000"

/**
 * Formats milliseconds into "mm:ss:SSS" format.
 *
 * @param timeMillis Elapsed time in milliseconds to format.
 * @return Formatted time string in "mm:ss:SSS" format.
 */
internal fun formatTime(timeMillis: Long): String {
    val absoluteNumber = abs(timeMillis)
    val totalSeconds = absoluteNumber / 1000
    val minutes = (totalSeconds / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')
    val milliseconds = (absoluteNumber % 1000).toString().padStart(3, '0')

    return if (timeMillis < 0)
        "-$minutes:$seconds:$milliseconds"
    else
        "$minutes:$seconds:$milliseconds"
}