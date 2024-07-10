package calendar.date_generation

import calendar.model.CalendarDate
import calendar.model.HijriYearInfo
import calendar.FIRST_DAY
import calendar.model.asHijri
import calendar.model.nextMonth
import calendar.model.number
import calendar.model.previousMonth
import exceptions.HijriYearNotFoundException
import internal.chooseDaysOfMonthWithMap
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import utils.dateToday

suspend fun Map<Int, HijriYearInfo>.getAdjacentMonth(
    currentDate: CalendarDate,
    vector: Vector
): Map<Int, CalendarDate> {

    val result = mutableMapOf<Int, CalendarDate>()

    val currentMonth = currentDate.hijriLocalDate.month
    val year = this[currentDate.hijriLocalDate.year]
        ?: throw HijriYearNotFoundException(currentDate.hijriLocalDate.year)

    var gregorianLocalDate = currentDate.gregorianLocalDate
    var hijriLocalDate = currentDate.hijriLocalDate

    return if (vector == Vector.FORTH) {
        val daysRemain =
            year.daysOfMonths[currentMonth.ordinal] - currentDate.hijriLocalDate.dayOfMonth
        gregorianLocalDate = gregorianLocalDate.plus(daysRemain, DateTimeUnit.DAY)
        val nextMonth = currentMonth.nextMonth()
        val nextYear =
            if (nextMonth.number == 1) this[year.year + 1] else null
        if (nextYear != null) {
            hijriLocalDate = hijriLocalDate.copy(
                year = nextYear.year,
                month = nextMonth,
                dayOfMonth = FIRST_DAY
            )
            for (d in 1..nextYear.daysOfMonths[nextMonth.ordinal]) {
                gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = d,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        } else {
            hijriLocalDate =
                hijriLocalDate.copy(month = nextMonth, dayOfMonth = FIRST_DAY)
            for (d in 1..year.daysOfMonths[nextMonth.ordinal]) {
                gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = d,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        }
        result
    } else {
        val pastMonth = currentMonth.previousMonth()
        val pastYear =
            if (pastMonth.number == 12) this[year.year - 1] else null
        if (pastYear != null) {
            hijriLocalDate = hijriLocalDate.copy(year = pastYear.year, month = pastMonth)
            for (d in pastYear.daysOfMonths[pastMonth.ordinal] downTo 1) {
                gregorianLocalDate = gregorianLocalDate.minus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = d,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        }
        result
    }
}

suspend fun Map<Int, HijriYearInfo>.getCurrentMonth(currentDate: CalendarDate? = null): Map<Int, CalendarDate> {
    return if (currentDate != null) {
        getMonth(currentDate)
    } else {
        val gregorianLocalDate = dateToday
        val hijriLocalDate = calculateHijriDate(gregorianLocalDate)
        val calendarDate = CalendarDate(gregorianLocalDate, hijriLocalDate)
        getMonth(calendarDate)
    }
}

suspend fun Map<Int, HijriYearInfo>.getNextMonth(currentDate: CalendarDate): Map<Int, CalendarDate> =
    getAdjacentMonth(currentDate, Vector.FORTH)

suspend fun Map<Int, HijriYearInfo>.getPreviousMonth(currentDate: CalendarDate): Map<Int, CalendarDate> =
    getAdjacentMonth(currentDate, Vector.BACK)

suspend fun Map<Int, HijriYearInfo>.getMonth(currentDate: CalendarDate): Map<Int, CalendarDate> {
    val chronologyOfCurrentYear = this[currentDate.hijriLocalDate.year]
        ?: throw HijriYearNotFoundException(currentDate.hijriLocalDate.year)
    val startDay = FIRST_DAY
    val ebdDay = chronologyOfCurrentYear.daysOfMonths[currentDate.hijriLocalDate.month.ordinal]
    return chooseDaysOfMonthWithMap(
        startDay,
        ebdDay,
        currentDate.gregorianLocalDate,
        currentDate.hijriLocalDate
    )
}

