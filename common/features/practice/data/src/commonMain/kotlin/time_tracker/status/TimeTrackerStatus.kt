package time_tracker.status

sealed interface TimeTrackerStatus {
    data object Started : TimeTrackerStatus
    data object Paused : TimeTrackerStatus
    data object Stopped : TimeTrackerStatus
}