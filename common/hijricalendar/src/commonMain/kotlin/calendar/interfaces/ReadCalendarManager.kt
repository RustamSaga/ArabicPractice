package calendar.interfaces

import calendar.model.CalendarDate
import calendar.model.SearchableHijriDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ReadCalendarManager {
    val today: CalendarDate

    suspend fun getDate(localDate: LocalDate): CalendarDate
    suspend fun getDate(hijriDate: SearchableHijriDate): CalendarDate

    fun getDatesRange(
        fromLocalDate: LocalDate,
        toLocalDate: LocalDate
    ): Flow<Pair<Int, CalendarDate>>

    fun getDatesRange(
        fromHijriDate: SearchableHijriDate,
        toHijriDate: SearchableHijriDate
    ): Flow<Pair<Int, CalendarDate>>


}