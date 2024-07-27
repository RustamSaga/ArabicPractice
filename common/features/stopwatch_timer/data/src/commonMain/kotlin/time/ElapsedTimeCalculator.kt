package time

import state.StopwatchState

/**
 * A calculator to compute elapsed time for the stopwatch.
 *
 * @param timestampProvider A provider to get the current timestamp.
 */
class ElapsedTimeCalculator(
    val timestampProvider: TimestampProviderApi
) {

    /**
     * Calculates the total elapsed time based on the current time and the state of the stopwatch.
     *
     * @param lastElapsedTime The last recorded elapsed time before the stopwatch was paused.
     * @return A new instance of [StopwatchState.Running] with updated elapsed time.
     */
    fun calculate(
        startTime: Long,
        lastElapsedTime: Long
    ): Long {
        val currentMillis = timestampProvider.currentMilliseconds
        val elapsedTime =
            (currentMillis - startTime).takeIf { currentMillis > startTime } ?: 0
        val totalTime = elapsedTime + lastElapsedTime

        return totalTime
    }
}