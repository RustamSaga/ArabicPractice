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
    private val startTime = MutableStateFlow(0L)

    /**
     * Stores the last elapsed time to ensure correct resumption of the timer after a pause.
     */
    private val lastElapsedTime = MutableStateFlow(0L)

    /**
     * Stores the limit of the timer in milliseconds.
     */
    private val limitMilliseconds = MutableStateFlow(0L)

    /**
     * Stores the current limit, which can be updated during the timer's operation.
     */
    private val currentLimit = MutableStateFlow(0L)
    override val limit: Long
        get() = limitMilliseconds.value

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
     * @throws IllegalStateException If the timer is running or paused.
     * @throws IllegalArgumentException If the limit is less than 1 second.
     */
    override fun setLimit(minutes: Int, seconds: Int) {
        if (mutableStatus.value::class == TimerState.Running::class ||
            mutableStatus.value::class == TimerState.Paused::class
        )
            throw IllegalStateException("Cannot perform this operation while the timer is running or paused.")

        val totalSeconds = minutes * 60 + seconds
        limitMilliseconds.value = totalSeconds * 1000L
        currentLimit.value = limitMilliseconds.value
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
                val elapsedTime = elapsedTimeCalculator.calculate(
                    startTime = startTime.value,
                    lastElapsedTime = lastElapsedTime.value
                )
                val remainingTime = currentLimit.value - elapsedTime
                mutableStatus.value = TimerState.Running(
                    timeMillisData = TimeMillisData(
                        formattedTime = formatTime(remainingTime),
                        elapsedTime = remainingTime
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
        lastElapsedTime.value = 0
        currentLimit.value = limitMilliseconds.value
        mutableStatus.value =
            TimerState.Stopped(
                TimeMillisData(
                    formattedTime = formatTime(currentLimit.value),
                    elapsedTime = currentLimit.value
                )
            )
    }

    /**
     * Resumes the timer from the last elapsed time.
     */
    private fun resume() {
        lastElapsedTime.value = mutableStatus.value.timeMillisData.elapsedTime
        currentLimit.value = lastElapsedTime.value
        mutableStatus.updateTo(TimerState.Running::class)
    }

    /**
     * Checks if the limit is valid.
     *
     * @throws IllegalArgumentException If the limit is less than 1000 milliseconds (1 second).
     */
    private fun limitCheck() {
        if (limitMilliseconds.value < 1000)
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
                startTime.value = elapsedTimeCalculator.timestampProvider.currentMilliseconds
                TimerState.Running(this.value.timeMillisData)
            }

            TimerState.Stopped::class -> TimerState.Stopped(this.value.timeMillisData)
            else -> throw IllegalArgumentException("Unsupported class: ${clazz.simpleName}")
        }
    }
}