package calendar.date_generation

import calendar.model.CalendarDate
import calendar.model.HijriYearInfo
import calendar.DAYS_OF_WEEK
import calendar.FIRST_DAY
import calendar.model.asHijri
import calendar.model.isoHijriDayNumber
import calendar.model.nextMonth
import calendar.model.number
import calendar.model.previousMonth
import exceptions.HijriYearNotFoundException
import internal.chooseDaysOfMonthWithMap
import internal.chooseDaysOfMonthWithMapDesc
import internal.isValidYear
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus

suspend fun Map<Int, HijriYearInfo>.getHijriWeek(calendarDate: CalendarDate): Map<Int, CalendarDate> {
    val result = mutableMapOf<Int, CalendarDate>()

    val localDate = calendarDate.gregorianLocalDate
    val currentHijriDate = calendarDate.hijriLocalDate
    val currentChronologyHijriYear = this[currentHijriDate.year]
        ?: throw HijriYearNotFoundException(currentHijriDate.year)

    val currentHijriMonth = currentHijriDate.month
    val currentHijriDay = currentHijriDate.dayOfMonth

    val maxDaysInCurrentMonth =
        currentChronologyHijriYear.daysOfMonths[currentHijriMonth.ordinal]
    val daysRemainToLastDayOfMonth = maxDaysInCurrentMonth - currentHijriDay


    val currentNumberDayOfWeek = currentHijriDate.dayOfWeek.isoHijriDayNumber
    val nextCountDaysOfWeek = DAYS_OF_WEEK - currentNumberDayOfWeek

    val isNextMonth = daysRemainToLastDayOfMonth < nextCountDaysOfWeek
    val isBackMonth = currentHijriDay <= currentNumberDayOfWeek

    if (isNextMonth) {
        var gregorianLocalDate = localDate
        var hijriLocalDate = currentHijriDate
        val isNextYear = currentHijriMonth.nextMonth().number == 1
        if (maxDaysInCurrentMonth >= currentHijriDay) {

            result.putAll(
                chooseDaysOfMonthWithMap(
                    currentHijriDay,
                    maxDaysInCurrentMonth,
                    localDate,
                    hijriLocalDate
                )
            )
            gregorianLocalDate = gregorianLocalDate.plus(
                maxDaysInCurrentMonth - currentHijriDay,
                DateTimeUnit.DAY
            )
        }
        if (isValidYear(currentChronologyHijriYear.year + 1)) {
            hijriLocalDate =
                hijriLocalDate.copy(
                    year = if (isNextYear) currentChronologyHijriYear.year + 1 else currentChronologyHijriYear.year,
                    month = currentHijriMonth.nextMonth()
                )
            result.putAll(
                chooseDaysOfMonthWithMap(
                    FIRST_DAY - 1,
                    nextCountDaysOfWeek,
                    gregorianLocalDate,
                    hijriLocalDate
                )
            )
        }
    } else {
        val lastDay = currentHijriDay + nextCountDaysOfWeek
        result.putAll(
            chooseDaysOfMonthWithMap(
                currentHijriDay,
                lastDay,
                localDate,
                currentHijriDate
            )
        )
    }

    if (isBackMonth) {
        var hijriLocalDate = currentHijriDate
        val isBackYear = currentHijriMonth.previousMonth().number == 12

        result.putAll(
            chooseDaysOfMonthWithMapDesc(
                currentHijriDay,
                FIRST_DAY,
                localDate,
                currentHijriDate,
                true
            )
        )
        if (isValidYear(currentChronologyHijriYear.year - 1)) {
            val lastDayOfMonth =
                this[currentChronologyHijriYear.year - 1]?.daysOfMonths?.last()
                    ?: throw HijriYearNotFoundException(currentChronologyHijriYear.year - 1)
            hijriLocalDate = hijriLocalDate.copy(
                year = if (isBackYear) currentChronologyHijriYear.year - 1 else currentChronologyHijriYear.year,
                month = currentHijriMonth.previousMonth()
            )
            val daysRemain = currentNumberDayOfWeek - currentHijriDay
            val lastDay = lastDayOfMonth - daysRemain
            result.putAll(
                chooseDaysOfMonthWithMapDesc(
                    lastDayOfMonth,
                    lastDay,
                    localDate,
                    hijriLocalDate,
                    false
                )
            )
        }
    } else {
        val lastDay = currentHijriDay - currentNumberDayOfWeek
        result.putAll(
            chooseDaysOfMonthWithMapDesc(
                currentHijriDay,
                lastDay,
                localDate,
                currentHijriDate,
                true
            )
        )
    }
    return result
}

suspend fun Map<Int, HijriYearInfo>.getAdjacentWeek(
    currentDate: CalendarDate,
    vector: Vector
): Map<Int, CalendarDate> {

    val result = mutableMapOf<Int, CalendarDate>()

    val currentDay = currentDate.hijriLocalDate.dayOfMonth
    val currentMonth = currentDate.hijriLocalDate.month.ordinal
    val year = this[currentDate.hijriLocalDate.year]
        ?: throw HijriYearNotFoundException(currentDate.hijriLocalDate.year)

    var gregorianLocalDate = currentDate.gregorianLocalDate
    var hijriLocalDate = currentDate.hijriLocalDate
    return if (vector == Vector.FORTH) {
        val maxDaysInCurrentMonth = year.daysOfMonths[currentMonth]
        val daysRemain = maxDaysInCurrentMonth - currentDay
        if (daysRemain >= DAYS_OF_WEEK) {
            for (d in currentDay + 1..currentDay + DAYS_OF_WEEK) {
                gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = d,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        } else {
            val nextMonth = currentDate.hijriLocalDate.month.nextMonth()
            val nextYear = if (nextMonth.number == 1) {
                this[currentDate.hijriLocalDate.year + 1]
                    ?: throw HijriYearNotFoundException(currentDate.hijriLocalDate.year + 1)
            } else null
            val lastDayOfWeek = DAYS_OF_WEEK - daysRemain

            for (dayOfCurrentMonth in currentDay + 1..maxDaysInCurrentMonth) {
                gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = dayOfCurrentMonth,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
            hijriLocalDate = hijriLocalDate.copy(
                year = nextYear?.year ?: hijriLocalDate.year,
                month = nextMonth
            )
            for (dayOfNextMonth in FIRST_DAY..lastDayOfWeek) {
                gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = dayOfNextMonth,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        }
        result
    } else {
        if (currentDay > DAYS_OF_WEEK) {
            for (d in currentDay - 1 downTo currentDay - DAYS_OF_WEEK) {
                gregorianLocalDate = gregorianLocalDate.minus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = d,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        } else if (currentDay <= DAYS_OF_WEEK) {
            val daysRemain = DAYS_OF_WEEK - currentDay
            val pastMonth = currentDate.hijriLocalDate.month.previousMonth()
            val pastYear = if (pastMonth.number == 12) {
                this[currentDate.hijriLocalDate.year - 1]
                    ?: throw HijriYearNotFoundException(currentDate.hijriLocalDate.year - 1)
            } else null

            val maxDaysOfPastMonth = year.daysOfMonths[pastMonth.ordinal]
            for (dayOfCurrentMonth in currentDay - 1 downTo FIRST_DAY) {
                gregorianLocalDate = gregorianLocalDate.minus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = dayOfCurrentMonth,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
            hijriLocalDate = hijriLocalDate.copy(
                year = pastYear?.year ?: hijriLocalDate.year,
                month = pastMonth
            )
            for (dayOfLastMonth in maxDaysOfPastMonth downTo maxDaysOfPastMonth - daysRemain) {
                gregorianLocalDate = gregorianLocalDate.minus(1, DateTimeUnit.DAY)
                hijriLocalDate = hijriLocalDate.copy(
                    dayOfMonth = dayOfLastMonth,
                    dayOfWeek = gregorianLocalDate.dayOfWeek.asHijri()
                )
                result[gregorianLocalDate.toEpochDays()] =
                    CalendarDate(gregorianLocalDate, hijriLocalDate)
            }
        }
        result
    }

}

suspend fun Map<Int, HijriYearInfo>.getNextWeek(currentDate: CalendarDate): Map<Int, CalendarDate> =
    getAdjacentWeek(currentDate, Vector.FORTH)

suspend fun Map<Int, HijriYearInfo>.getPreviousWeek(currentDate: CalendarDate): Map<Int, CalendarDate> =
    getAdjacentWeek(currentDate, Vector.BACK)
