package calendar.interfaces

import calendar.model.CalendarDate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface YearManager {

    val today: CalendarDate

    suspend fun initCalendarYear()
    fun calendarFlow(): StateFlow<Map<Int, CalendarDate>>



    suspend fun generateDates(startEpochDays: Int, endEpochDays: Int)

    suspend fun generateDates(startLocalDate: LocalDate, endLocalDate: LocalDate)


}