package stopwatch

import kotlinx.coroutines.flow.StateFlow
import state.StopwatchState

interface StopwatchApi {
    val status: StateFlow<StopwatchState>
    fun start()
    fun pause()
    fun stop()
    fun reset()
}