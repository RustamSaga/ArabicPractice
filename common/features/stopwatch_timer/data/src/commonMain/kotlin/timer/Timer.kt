package timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import model.TimeMillisData
import state.TimerState
import time.ElapsedTimeCalculator
import utils.formatTime
import kotlin.reflect.KClass

/**
 * Implementation of a timer that provides start, pause, stop, and reset functionalities.
 * The timer runs in a given [CoroutineScope] and calculates elapsed time using an [ElapsedTimeCalculator].
 *
 * @param elapsedTimeCalculator A calculator to compute elapsed time based on the current time.
 * @param scope The coroutine scope in which the timer will run.
 */
class Timer(
    private val elapsedTimeCalculator: ElapsedTimeCalculator,
    private val scope: CoroutineScope
) : TimerApi {

    private var job: Job? = null

    /**
     * Stores the time when the timer was last started or resumed.
     */
    private var startTime = 0L

    /**
     * Stores the last elapsed time to ensure correct resumption of the timer after a pause.
     */
    private var lastElapsedTime = 0L

    private var lastCalculateMilliseconds = 0L

    /**
     * Stores the limit of the timer in milliseconds.
     */
    private var limitMilliseconds = 0L

    /**
     * Stores the current limit, which can be updated during the timer's operation.
     */
    private var currentLimit = 0L
    override val limit: Long
        get() = limitMilliseconds

    /**
     * Stores the current state of the timer.
     */
    private val mutableStatus = MutableStateFlow<TimerState>(TimerState.newStopped())
    override val status: StateFlow<TimerState>
        get() = mutableStatus

    /**
     * Sets the limit of the timer.
     *
     * @param minutes The minutes part of the limit.
     * @param seconds The seconds part of the limit.
     * @throws IllegalArgumentException If the limit is less than 1 second.
     */
    override fun setLimit(minutes: Int, seconds: Int) {
        if (mutableStatus.value::class == TimerState.Running::class ||
            mutableStatus.value::class == TimerState.Paused::class
        ) return

        val totalSeconds = minutes * 60 + seconds
        limitMilliseconds = totalSeconds * 1000L
        currentLimit = limitMilliseconds
    }

    /**
     * Starts the timer. If it is already running, it does nothing.
     * If it is paused, it resumes from the last elapsed time.
     * If it is stopped, it resets and starts from zero.
     *
     * @throws IllegalArgumentException If the limit is less than 1 second.
     */
    override fun start() {
        when (mutableStatus.value) {
            is TimerState.Running -> Unit
            is TimerState.Paused -> {
                resume()
                startJob()
            }

            is TimerState.Stopped -> {
                limitCheck()
                reset()
                mutableStatus.updateTo(TimerState.Running::class)
                startJob()
            }
        }
    }

    /**
     * Launches a coroutine that updates the timer state every 10 milliseconds.
     */
    private fun startJob() {
        if (job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                lastCalculateMilliseconds = elapsedTimeCalculator.calculate(
                    startTime = startTime,
                    lastElapsedTime = lastElapsedTime
                )
                val remainingTime = currentLimit - lastCalculateMilliseconds
                mutableStatus.value = TimerState.Running(
                    timeMillisData = TimeMillisData(
                        formattedTime = formatTime(remainingTime),
                        timeMillis = remainingTime
                    )
                )
                delay(10L)
            }
        }
    }

    override fun pause() {
        job?.cancel()
        job = null
        mutableStatus.updateTo(TimerState.Paused::class)
    }

    override fun stop() {
        job?.cancel()
        job = null
        mutableStatus.updateTo(TimerState.Stopped::class)
    }

    override fun reset() {
        lastElapsedTime = 0
        currentLimit = limitMilliseconds
        mutableStatus.value =
            TimerState.Stopped(
                TimeMillisData(
                    formattedTime = formatTime(currentLimit),
                    timeMillis = currentLimit
                )
            )
    }

    /**
     * Resumes the timer from the last elapsed time.
     */
    private fun resume() {
        lastElapsedTime = lastCalculateMilliseconds
        mutableStatus.updateTo(TimerState.Running::class)
    }

    /**
     * Checks if the limit is valid.
     *
     * @throws IllegalArgumentException If the limit is less than 1000 milliseconds (1 second).
     */
    private fun limitCheck() {
        if (limitMilliseconds < 1000)
            throw IllegalArgumentException("The timer limit must be at least 1000 milliseconds (1 second).")
    }

    /**
     * Updates the current state of the timer.
     *
     * @param clazz The class of the new timer state.
     * @throws IllegalArgumentException If the class is not supported.
     */
    private fun StateFlow<TimerState>.updateTo(clazz: KClass<out TimerState>) {
        mutableStatus.value = when (clazz) {
            TimerState.Paused::class -> TimerState.Paused(this.value.timeMillisData)
            TimerState.Running::class -> {
                startTime = elapsedTimeCalculator.timestampProvider.currentMilliseconds
                TimerState.Running(this.value.timeMillisData)
            }

            TimerState.Stopped::class -> TimerState.Stopped(this.value.timeMillisData)
            else -> throw IllegalArgumentException("Unsupported class: ${clazz.simpleName}")
        }
    }
}