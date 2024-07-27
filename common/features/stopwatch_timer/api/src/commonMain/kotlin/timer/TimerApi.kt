package timer

import kotlinx.coroutines.flow.StateFlow
import state.TimerState

interface TimerApi {
    val status: StateFlow<TimerState>
    val limit: Long
    fun setLimit(minutes: Int, seconds: Int)
    fun start()
    fun pause()
    fun stop()
    fun reset()
}