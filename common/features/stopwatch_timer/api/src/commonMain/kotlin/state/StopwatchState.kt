package state

import model.TimeMillisData

/**
 * Represents the state of a stopwatch, which can be either Running, Paused, or Stopped.
 */
sealed interface StopwatchState {
    val timeMillisData: TimeMillisData

    /**
     * Represents the running state of the stopwatch.
     *
     * @property timeMillisData The time data associated with the stopwatch.
     */
    data class Running(
        override val timeMillisData: TimeMillisData,
    ) : StopwatchState

    /**
     * Represents the paused state of the stopwatch.
     *
     * @property timeMillisData The time data associated with the stopwatch.
     */
    data class Paused(
        override val timeMillisData: TimeMillisData,
    ) : StopwatchState

    /**
     * Represents the stopped state of the stopwatch.
     *
     * @property timeMillisData The time data associated with the stopwatch.
     * This data is preserved when the stopwatch enters this state.
     * However, it is reset when the stopwatch is started again.
     */
    data class Stopped(
        override val timeMillisData: TimeMillisData,
    ) : StopwatchState

    companion object {
        fun newStopped() : Stopped = Stopped(timeMillisData = TimeMillisData.newInstance())
    }
}