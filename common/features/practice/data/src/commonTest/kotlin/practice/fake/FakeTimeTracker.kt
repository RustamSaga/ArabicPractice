package practice.fake

import kotlinx.coroutines.flow.StateFlow
import model.TimeMillisData
import practice.reading.options.ReadingSpeedTimer
import time_tracker.interfaces.TimeTracker
import time_tracker.status.TimeTrackerStatus

class FakeTimeTracker: TimeTracker {

    private var timerCount = 0
    private var stopwatch = 0
    private var timerIsStarted = false
    override val trackerStatus: StateFlow<TimeTrackerStatus>
        get() = TODO("Not yet implemented")
    override val stopwatchCurrentTime: StateFlow<TimeMillisData>
        get() = TODO("Not yet implemented")
    override val timerCurrentTime: StateFlow<TimeMillisData>
        get() = TODO("Not yet implemented")

    override fun setLimit(readingSpeedTimer: ReadingSpeedTimer) {
        TODO("Not yet implemented")
    }


    override suspend fun start(
        stopwatchCurrentTime: (TimeMillisData) -> Unit,
        timerCurrentTime: (TimeMillisData) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun resume() {
        TODO("Not yet implemented")
    }

    override suspend fun reset() {
        TODO("Not yet implemented")
    }

    override suspend fun pause() {
        TODO("Not yet implemented")
    }

    override suspend fun stop() {
        TODO("Not yet implemented")
    }

    override fun isStopwatchRunning(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTimerRunning(): Boolean {
        TODO("Not yet implemented")
    }

}