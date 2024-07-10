package calendar.model

import kotlinx.datetime.LocalDate

data class CalendarDate(
    val gregorianLocalDate: LocalDate,
    val hijriLocalDate: HijriLocalDate
)

sealed interface ICalendarDate {
    data class SynchronizedCalendar(
        val gregorianLocalDate: LocalDate,
        val hijriLocalDate: HijriLocalDate
    ): ICalendarDate
    data class GregorianDate(val localDate: LocalDate): ICalendarDate
}

