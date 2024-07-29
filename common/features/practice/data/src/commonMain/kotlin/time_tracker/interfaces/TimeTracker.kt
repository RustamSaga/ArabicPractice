package time_tracker.interfaces

import kotlinx.coroutines.flow.StateFlow
import model.TimeMillisData
import practice.reading.options.ReadingSpeedTimer
import time_tracker.status.TimeTrackerStatus

interface TimeTracker {

    val trackerStatus: StateFlow<TimeTrackerStatus>
    val stopwatchCurrentTime: StateFlow<TimeMillisData>
    val timerCurrentTime: StateFlow<TimeMillisData>

    fun setLimit(readingSpeedTimer: ReadingSpeedTimer)

    suspend fun start(
        stopwatchCurrentTime: (TimeMillisData) -> Unit = {},
        timerCurrentTime: (TimeMillisData) -> Unit = {},
    )
    suspend fun resume()
    suspend fun reset()
    suspend fun pause()
    suspend fun stop()
    fun isStopwatchRunning(): Boolean
    fun isTimerRunning(): Boolean
}