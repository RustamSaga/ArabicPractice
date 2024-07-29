package practice.reading.tracker

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import model.TimeMillisData
import practice.reading.options.TrainingOptions
import practice.reading.result.ReadingTrackerResult

interface ReadingTracker {

    val content: List<String>
    val option: TrainingOptions
    val wordsFromMic: SharedFlow<String>
    val stopwatchCurrentTime: StateFlow<TimeMillisData>
    val timerCurrentTime: StateFlow<TimeMillisData>
    val isFinished: StateFlow<Boolean>

    fun getElapsedTime(): Long

    fun getResult(): ReadingTrackerResult?
    fun stopwatchResult(): TimeMillisData?
    fun timerResult(): TimeMillisData?

    fun start()
    fun start(
        stopwatchCurrentTime: (formattedTime: String, timeMillis: Long) -> Unit,
        timerCurrentTime: (formattedTime: String, timeMillis: Long) -> Unit,
        currentWord: (word: String) -> Unit,
        ifFinished: () -> Unit
    )
    suspend fun pause()
    suspend fun resume()
    suspend fun finish()
}

