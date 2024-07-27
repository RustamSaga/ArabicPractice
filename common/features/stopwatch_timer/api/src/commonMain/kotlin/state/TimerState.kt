package state

import model.TimeMillisData

/**
 * Interface representing the state of a timer.
 * The timer can be in different states: running, paused, or stopped.
 */
sealed interface TimerState {
    /**
     * Data about the time associated with the current timer state.
     */
    val timeMillisData: TimeMillisData

    /**
     * The state of the timer when it is running.
     *
     * @property timeMillisData Data about the elapsed time since the timer started running.
     */
    data class Running(
        override val timeMillisData: TimeMillisData,
    ) : TimerState

    /**
     * The state of the timer when it is paused.
     *
     * @property timeMillisData Data about the elapsed time before the timer was paused.
     */
    data class Paused(
        override val timeMillisData: TimeMillisData,
    ) : TimerState

    /**
     * The state of the timer when it is stopped.
     *
     * @property timeMillisData Data about the elapsed time before the timer was stopped.
     */
    data class Stopped(
        override val timeMillisData: TimeMillisData,
    ) : TimerState

    companion object {
        /**
         * Creates a new timer state representing a stopped timer.
         *
         * @return A `Stopped` object with initialized time data.
         */
        fun newStopped() : Stopped = Stopped(timeMillisData = TimeMillisData.newInstance())
    }
}