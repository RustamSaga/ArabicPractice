package calendar.interfaces

import calendar.model.CalendarDate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface MonthManager {

    val today: CalendarDate

    suspend fun initCalendarMonth()
    fun calendarFlow(): StateFlow<Map<Int, CalendarDate>>



    suspend fun generateDates(startLocalDate: LocalDate, endLocalDate: LocalDate)


    suspend fun generateDates(startEpochDays: Int, endEpochDays: Int)

}