package calendar.interfaces

import calendar.model.CalendarDate
import kotlinx.datetime.LocalDate

interface CalendarManager : ReadCalendarManager, DataManager, InitManager {

    suspend fun generateDates(startEpochDays: Int, endEpochDays: Int)
    suspend fun generateDates(startLocalDate: LocalDate, endLocalDate: LocalDate)
    suspend fun generateDates(
        startEpochDays: Int,
        nextDays: Int,
        result: suspend (Pair<Int, CalendarDate>) -> Unit
    )
}
