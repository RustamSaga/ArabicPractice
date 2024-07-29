package practice.reading.options

import utils.getSeconds

/**
 *  An instance of ReadingSpeedTimer that sets the limit for the timer.
 *  Depending on the chosen speech speed, this entity calculates the time
 *  required to read the text. To do this, you need to pass the number
 *  of words in the text when creating this entity so that it can perform
 *  the calculations.
 */
sealed interface ReadingSpeedTimer {
    val speed: Int
    val limitSecond: Int
    suspend fun ifModeEnabled(block: suspend () -> Unit): Boolean {
        block()
        return true
    }
    data class HighNativeSpeedTimer (private val numberOfWords: Int): ReadingSpeedTimer {
        override val speed: Int = 250
        override val limitSecond: Int = numberOfWords.getSeconds(speed)
    }

    data class MinimumNativeSpeedTimer(private val numberOfWords: Int): ReadingSpeedTimer {
        override val speed: Int = 150
        override val limitSecond: Int = numberOfWords.getSeconds(speed)
    }
    data class CustomSpeedTimer(private val numberOfWords: Int, private val customSpeed: Int): ReadingSpeedTimer {
        override val speed: Int = customSpeed
        override val limitSecond: Int = numberOfWords.getSeconds(speed)
    }

    data object NoTimer : ReadingSpeedTimer {
        override val speed: Int = 0
        override val limitSecond: Int = 0
        override suspend fun ifModeEnabled(block: suspend () -> Unit) = false

    }
}