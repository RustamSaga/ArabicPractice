package stopwatch

import time.ElapsedTimeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import model.TimeMillisData
import state.StopwatchState
import utils.formatTime
import kotlin.reflect.KClass

/**
 * Implementation of a stopwatch that provides start, pause, stop, and reset functionalities.
 * The stopwatch runs in a given [CoroutineScope] and calculates elapsed time using an [ElapsedTimeCalculator].
 *
 * @param elapsedTimeCalculator A calculator to compute elapsed time based on the current time.
 * @param scope The coroutine scope in which the stopwatch will run.
 */
class Stopwatch(
    private val elapsedTimeCalculator: ElapsedTimeCalculator,
    private val scope: CoroutineScope,
) : StopwatchApi {

    private var job: Job? = null

    /**
     * Stores the last elapsed time to ensure correct resumption of the stopwatch after a pause.
     */
    private val lastElapsedTime = MutableStateFlow(0L)
    /**
     * Stores the time when the stopwatch was last started or resumed.
     */
    private val startTime = MutableStateFlow(0L)

    private val mutableStatus = MutableStateFlow<StopwatchState>(StopwatchState.newStopped())
    override val status: StateFlow<StopwatchState>
        get() = mutableStatus
    /**
     * Starts the stopwatch. If it is already running, it does nothing.
     * If it is paused, it resumes from the last elapsed time.
     * If it is stopped, it resets and starts from zero.
     */
    override fun start() {
        when (mutableStatus.value) {
            is StopwatchState.Running -> Unit
            is StopwatchState.Paused -> {
                resume()
                startJob()
            }
            is StopwatchState.Stopped -> {
                reset()
                mutableStatus.updateTo(StopwatchState.Running::class)
                startJob()
            }
        }
    }

    /**
     * Launches a coroutine that updates the stopwatch state every 10 milliseconds.
     */
    private fun startJob() {
        if (job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                val elapsedTime = elapsedTimeCalculator.calculate(
                    startTime.value,
                    lastElapsedTime.value
                )
                mutableStatus.value = StopwatchState.Running(
                    timeMillisData = TimeMillisData(
                        formattedTime = formatTime(elapsedTime),
                        elapsedTime = elapsedTime
                    )
                )
                delay(10L)
            }
        }
    }

    override fun pause() {
        job?.cancel()
        job = null
        mutableStatus.updateTo(StopwatchState.Paused::class)
    }

    override fun stop() {
        job?.cancel()
        job = null
        mutableStatus.updateTo(StopwatchState.Stopped::class)
    }

    private fun resume() {
        lastElapsedTime.value = mutableStatus.value.timeMillisData.elapsedTime
        mutableStatus.updateTo(StopwatchState.Running::class)
    }

    override fun reset() {
        lastElapsedTime.value = 0
        mutableStatus.value = StopwatchState.newStopped()
    }

    private fun StateFlow<StopwatchState>.updateTo(clazz: KClass<out StopwatchState>) {
        mutableStatus.value = when (clazz) {
            StopwatchState.Paused::class -> StopwatchState.Paused(this.value.timeMillisData)
            StopwatchState.Running::class -> {
                startTime.value = elapsedTimeCalculator.timestampProvider.currentMilliseconds
                StopwatchState.Running(this.value.timeMillisData)
            }
            StopwatchState.Stopped::class -> StopwatchState.Stopped(this.value.timeMillisData)
            else -> throw IllegalArgumentException("Unsupported class: ${clazz.simpleName}")
        }
    }
}