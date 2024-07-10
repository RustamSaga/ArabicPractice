package internal

import calendar.model.CalendarDate
import calendar.model.HijriLocalDate
import calendar.model.asHijri
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

internal fun chooseDaysOfMonth(
    startDay: Int,
    endDay: Int,
    gregorianDate: LocalDate,
    hijriDate: HijriLocalDate
): List<CalendarDate> {
    val result = mutableListOf<CalendarDate>()
    var gregorianLocalDate = gregorianDate
    var hijriLocalDate = hijriDate
    for (d in startDay + 1..endDay) {
        gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
        hijriLocalDate = hijriLocalDate.copy(
            dayOfMonth = d,
            dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
        )
        result.add(CalendarDate(gregorianLocalDate, hijriLocalDate))
    }
    return result
}
internal fun chooseDaysOfMonthWithMap(
    startDay: Int,
    endDay: Int,
    gregorianDate: LocalDate,
    hijriDate: HijriLocalDate
): Map<Int, CalendarDate> {
    val result = mutableMapOf<Int, CalendarDate>()
    var gregorianLocalDate = gregorianDate
    var hijriLocalDate = hijriDate
    for (d in startDay + 1..endDay) {
        gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
        val epochDays = gregorianLocalDate.toEpochDays()
        hijriLocalDate = hijriLocalDate.copy(
            dayOfMonth = d,
            dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
        )
        result[epochDays] = CalendarDate(gregorianLocalDate, hijriLocalDate)
    }
    return result
}

internal fun chooseDaysOfMonthDesc(
    startDay: Int,
    endDay: Int,
    gregorianDate: LocalDate,
    hijriDate: HijriLocalDate,
    isFirstIterate: Boolean
): List<CalendarDate> {
    val result = mutableListOf<CalendarDate>()
    var gregorianLocalDate = gregorianDate
    var hijriLocalDate = hijriDate

    var currentValue = startDay

    while ((startDay != endDay && currentValue > endDay) || (startDay == endDay && currentValue == endDay)) {
        gregorianLocalDate =
            if (isFirstIterate && currentValue == startDay) gregorianLocalDate
            else gregorianLocalDate.minus(1, DateTimeUnit.DAY)
        hijriLocalDate = hijriLocalDate.copy(
            dayOfMonth = currentValue,
            dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
        )
        result.add(0, CalendarDate(gregorianLocalDate, hijriLocalDate))
        currentValue--
    }
    return result
}

internal fun chooseDaysOfMonthWithMapDesc(
    startDay: Int,
    endDay: Int,
    gregorianDate: LocalDate,
    hijriDate: HijriLocalDate,
    isFirstIterate: Boolean
): Map<Int, CalendarDate> {
    val result = mutableMapOf<Int, CalendarDate>()
    var gregorianLocalDate = gregorianDate
    var hijriLocalDate = hijriDate

    var currentValue = startDay

    while ((startDay != endDay && currentValue > endDay) || (startDay == endDay && currentValue == endDay)) {
        gregorianLocalDate =
            if (isFirstIterate && currentValue == startDay) gregorianLocalDate
            else gregorianLocalDate.minus(1, DateTimeUnit.DAY)
        hijriLocalDate = hijriLocalDate.copy(
            dayOfMonth = currentValue,
            dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
        )
        result[gregorianDate.toEpochDays()] = CalendarDate(gregorianLocalDate, hijriLocalDate)
        currentValue--
    }
    return result
}

