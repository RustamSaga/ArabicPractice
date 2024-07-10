package calendar

import calendar.interfaces.ReadCalendarManager
import calendar.model.CalendarDate
import calendar.model.SearchableHijriDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import model.Location
import sunset_source.interfaces.SunsetTimeProvider

class SyncCalendarsProvider(
    private val readCalendarManager: ReadCalendarManager,
    private val sunsetTimeProvider: SunsetTimeProvider
) {

    private var _calendarMode = CalendarMode.GREGORIAN
    val calendarMode = _calendarMode

    fun updateCalendarMode(mode: CalendarMode): CalendarMode {
        _calendarMode = mode
        return _calendarMode
    }

    suspend fun today(location: Location? = null): CalendarDate {
        val currentDate = readCalendarManager.today
        return if (_calendarMode == CalendarMode.GREGORIAN) {
            currentDate
        } else {
            if (sunsetTimeProvider.isSunsetTime(location)) {
                val gregorianLocalDate = currentDate.gregorianLocalDate.plus(1, DateTimeUnit.DAY)
                getDate(gregorianLocalDate)
            } else {
                currentDate
            }
        }
    }


    suspend fun getDate(localDate: LocalDate): CalendarDate = readCalendarManager.getDate(localDate)
    suspend fun getDate(hijriDate: SearchableHijriDate): CalendarDate = readCalendarManager.getDate(hijriDate)

    fun getDateRange(
        fromLocalDate: LocalDate,
        toLocalDate: LocalDate
    ): Flow<Pair<Int, CalendarDate>> = readCalendarManager.getDatesRange(fromLocalDate, toLocalDate)

    fun getDateRange(
        fromHijriDate: SearchableHijriDate,
        toHijriDate: SearchableHijriDate
    ): Flow<Pair<Int, CalendarDate>> = readCalendarManager.getDatesRange(fromHijriDate, toHijriDate)

}