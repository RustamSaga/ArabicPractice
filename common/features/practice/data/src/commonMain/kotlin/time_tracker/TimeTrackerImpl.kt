package time_tracker

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.TimeMillisData
import practice.reading.options.ReadingSpeedTimer
import state.StopwatchState
import state.TimerState
import stopwatch.StopwatchApi
import time_tracker.interfaces.TimeTracker
import time_tracker.status.TimeTrackerStatus
import timer.TimerApi

/**
 * Implementation of the TimeTracker interface, managing both a stopwatch and a timer.
 *
 *  ***Note:  By default, the timer is disabled. To set a timer limit, use the setLimit function.
 *  Note that the timer does not stop the tracker when it reaches zero; you need to implement
 *  your own logic for stopping the tracker. The timer will continue to count negatively
 *  after reaching zero until it is cancelled.***
 *
 * @property stopwatch The StopwatchApi instance to manage stopwatch operations.
 * @property timer The TimerApi instance to manage timer operations. By default, the timer is disabled
 */
class TimeTrackerImpl(
    private val stopwatch: StopwatchApi,
    private val timer: TimerApi
) : TimeTracker {

    // Job representing the coroutine collecting stopwatch status updates
    private var stopwatchJob: Job? = null

    // Job representing the coroutine collecting timer status updates
    private var timerJob: Job? = null

    // Instance of ReadingSpeedTimer to manage reading speed limits.
    // It provides the timer with the time within which the user should read the entire text
    private var readingSpeedTimer: ReadingSpeedTimer = ReadingSpeedTimer.NoTimer

    // MutableStateFlow to manage the status of the tracker
    private val mutableTrackerStatus =
        MutableStateFlow<TimeTrackerStatus>(TimeTrackerStatus.Stopped)

    // Public StateFlow to expose the tracker status
    override val trackerStatus: StateFlow<TimeTrackerStatus> get() = mutableTrackerStatus

    // MutableStateFlow to manage the current time of the stopwatch
    private val mutableStopwatchCurrentTime =
        MutableStateFlow(stopwatch.status.value.timeMillisData)

    // Public StateFlow to expose the current time of the stopwatch
    override val stopwatchCurrentTime: StateFlow<TimeMillisData>
        get() = mutableStopwatchCurrentTime

    // MutableStateFlow to manage the current time of the timer
    private val mutableTimerCurrentTime = MutableStateFlow(timer.status.value.timeMillisData)

    // Public StateFlow to expose the current time of the timer
    override val timerCurrentTime: StateFlow<TimeMillisData>
        get() = mutableTimerCurrentTime

    /**
     * Sets the reading speed limit for the timer.
     *
     * @param readingSpeedTimer The ReadingSpeedTimer instance defining the limit.
     */
    override fun setLimit(readingSpeedTimer: ReadingSpeedTimer) {
        this.readingSpeedTimer = readingSpeedTimer
    }

    /**
     * Starts the time tracking, including both the stopwatch and timer if enabled.
     *
     * @param stopwatchCurrentTime Lambda to handle the current time of the stopwatch.
     * @param timerCurrentTime Lambda to handle the current time of the timer.
     * @throws IllegalArgumentException If the limit is less than 1 second.
     */
    override suspend fun start(
        stopwatchCurrentTime: (TimeMillisData) -> Unit,
        timerCurrentTime: (TimeMillisData) -> Unit
    ) {
        if (isStopwatchRunning()) return

        mutableTrackerStatus.value = TimeTrackerStatus.Started
        stopwatch.start()
        readingSpeedTimer.ifModeEnabled {
            timer.setLimit(
                minutes = 0,
                seconds = readingSpeedTimer.limitSecond
            )
            timer.start()
        }

        coroutineScope {
            stopwatchJob = launch(CoroutineName("TimeTracker: stopwatch")) {
                stopwatch.status.collect { state ->
                    mutableStopwatchCurrentTime.value = state.timeMillisData
                    stopwatchCurrentTime(state.timeMillisData)
                }
            }
            readingSpeedTimer.ifModeEnabled {
                timerJob = launch(CoroutineName("TimeTracker: timer")) {
                    timer.status.collect { state ->
                        mutableTimerCurrentTime.value = state.timeMillisData
                        timerCurrentTime(state.timeMillisData)
                    }
                }
            }

        }
    }

    /**
     * Resumes the time tracking if it was paused.
     */
    override suspend fun resume() {
        if (mutableTrackerStatus.value::class != TimeTrackerStatus.Paused::class) return
        stopwatch.start()
        readingSpeedTimer.ifModeEnabled { timer.start() }
        if (mutableTrackerStatus.value::class != TimeTrackerStatus.Started::class) {
            mutableTrackerStatus.value = TimeTrackerStatus.Started
        }
    }

    /**
     * Pauses the time tracking.
     */
    override suspend fun pause() {
        if (mutableTrackerStatus.value::class != TimeTrackerStatus.Started::class) return
        stopwatch.pause()
        readingSpeedTimer.ifModeEnabled { timer.pause() }
        mutableTrackerStatus.value = TimeTrackerStatus.Paused
    }

    /**
     * Stops the time tracking and cancels any active jobs.
     */
    override suspend fun stop() {
        if (mutableTrackerStatus.value::class == TimeTrackerStatus.Stopped::class) return
        stopwatch.stop()
        readingSpeedTimer.ifModeEnabled { timer.stop() }
        stopwatchJob?.cancel()
        timerJob?.cancel()
        timerJob = null
        stopwatchJob = null
        mutableTrackerStatus.value = TimeTrackerStatus.Stopped
    }

    /**
     * Resets the stopwatch and timer.
     */
    override suspend fun reset() {
        stopwatch.reset()
        readingSpeedTimer.ifModeEnabled { timer.reset() }
    }

    /**
     * Checks if the stopwatch is currently running.
     *
     * @return True if the stopwatch is running, otherwise false.
     */
    override fun isStopwatchRunning(): Boolean =
        stopwatch.status.value::class == StopwatchState.Running::class

    /**
     * Checks if the timer is currently running.
     *
     * @return True if the timer is running, otherwise false.
     */
    override fun isTimerRunning(): Boolean =
        timer.status.value::class == TimerState.Running::class
}