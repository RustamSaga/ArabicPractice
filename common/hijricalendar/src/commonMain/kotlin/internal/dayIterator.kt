package internal

import calendar.DAYS_PASSED_IN_YEAR
import calendar.DAYS_REMAINING_IN_YEAR
import calendar.FIRST_DAY
import calendar.MAX_GREGORIAN_DATE_IN_HIJRI
import calendar.MIN_GREGORIAN_DATE_IN_HIJRI
import calendar.NUMBER_OF_FIRST_MONTH
import calendar.TOTAL_HIJRI_MONTHS
import calendar.date_generation.calculateHijriDate
import calendar.interfaces.DataManager
import calendar.model.CalendarDate
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import calendar.model.HijriYearInfo
import calendar.model.asHijri
import calendar.model.nextMonth
import calendar.model.number
import calendar.model.previousMonth
import exceptions.GregorianDateOutOfHijriRangeException
import exceptions.HijriDateNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

internal const val ONE_DAY = 1

/**
 * @exception GregorianDateOutOfHijriRangeException If localDate is after 2174-11-23
 * @exception HijriDateNotFoundException The exception shouldn't occur, but if it did, there might be an error in the JSON file.
 */
internal fun goToForth(
    chronology: Map<Int, HijriYearInfo>,
    _days: Int,
    _referenceHijriDay: Int,
    _referenceHijriMonth: HijriMonth,
    localDate: LocalDate,
): HijriLocalDate {

    if (localDate > MAX_GREGORIAN_DATE_IN_HIJRI)
        throw GregorianDateOutOfHijriRangeException(localDate)

    var days = _days
    var referenceHijriDay = _referenceHijriDay
    var referenceHijriMonth = _referenceHijriMonth
    var daysRemainingInYear = DAYS_REMAINING_IN_YEAR

    for (yearIndex in chronology) {
        val year = yearIndex.value

        if (days > daysRemainingInYear) {
            days -= daysRemainingInYear
            referenceHijriDay = 0
            referenceHijriMonth = HijriMonth.MUHARRAM
            daysRemainingInYear = chronology.getValue(year.year + 1).daysOfYear
            continue
        }
        // month number has from 1 to 12. Total number is 12
        for (numberOfMonth in referenceHijriMonth.number..TOTAL_HIJRI_MONTHS) {
            val daysOfMonth = year.daysOfMonths[numberOfMonth - 1] // because index begin from 0
            val daysUntilTheEndOfTheMonth =
                if (referenceHijriDay == 0) daysOfMonth else daysOfMonth - referenceHijriDay

            if (days > daysUntilTheEndOfTheMonth) {
                days -= daysUntilTheEndOfTheMonth
                referenceHijriDay = 0
                continue
            }
            for (day in referenceHijriDay..daysOfMonth) {
                if (days > 0) {
                    days--
                } else {
                    return HijriLocalDate(
                        year = year.year,
                        month = HijriMonth(numberOfMonth),
                        dayOfMonth = day,
                        dayOfWeek = localDate.dayOfWeek.asHijri()
                    )
                }
            }
        }
        referenceHijriMonth = HijriMonth(NUMBER_OF_FIRST_MONTH)
    }
    throw HijriDateNotFoundException(
        "Date not found: ${localDate.year}/${localDate.month}/${localDate.dayOfMonth}, Check your JSON file hijrichronology.json"
    )
}

/**
 * @exception GregorianDateOutOfHijriRangeException If localDate is before 1882-11-11
 * @exception HijriDateNotFoundException The exception shouldn't occur, but if it did, there might be an error in the JSON file.
 */
internal fun goToBack(
    chronology: Map<Int, HijriYearInfo>,
    _days: Int,
    _referenceHijriDay: Int,
    _referenceHijriMonth: HijriMonth,
    localDate: LocalDate,
): HijriLocalDate {

    if (localDate < MIN_GREGORIAN_DATE_IN_HIJRI)
        throw GregorianDateOutOfHijriRangeException(localDate)


    var days = _days
    var referenceHijriDay = _referenceHijriDay
    var referenceHijriMonth = _referenceHijriMonth
    var passed_days = DAYS_PASSED_IN_YEAR

    for (yearIndex in chronology) {
        val year = yearIndex.value

        if (days > passed_days) {
            days -= passed_days
            val nextYear = chronology.getValue(year.year - 1)
            passed_days = nextYear.daysOfYear
            referenceHijriDay = nextYear.daysOfMonths.last()
            referenceHijriMonth = HijriMonth.ZU_AL_HIJJAH
            continue
        }
        for (numberOfMonth in referenceHijriMonth.number downTo NUMBER_OF_FIRST_MONTH) {
            referenceHijriMonth = HijriMonth(numberOfMonth)
            if (days > referenceHijriDay) {
                days -= referenceHijriDay
                val beforeMonth = numberOfMonth - 1
                val daysInBeforeMonth = year.daysOfMonths[beforeMonth - 1]
                referenceHijriDay = daysInBeforeMonth
                continue
            }
            for (day in referenceHijriDay downTo FIRST_DAY) {
                if (days > 0) {
                    days--
                } else {
                    return HijriLocalDate(
                        year = year.year,
                        month = referenceHijriMonth,
                        dayOfMonth = day,
                        dayOfWeek = localDate.dayOfWeek.asHijri()
                    )
                }
            }
        }
    }
    throw HijriDateNotFoundException(
        "Date not found: ${localDate.year}/${localDate.month}/${localDate.dayOfMonth}, Check your JSON file hijrichronology.json"
    )
}

/**
 * @exception GregorianDateOutOfHijriRangeException If localDate is before 1882-11-11
 * @exception HijriDateNotFoundException The exception shouldn't occur, but if it did, there might be an error in the JSON file.
 */
internal fun DataManager.getDaysFlow(
    startEpochDays: Int,
    endEpochDays: Int,
): Flow<Pair<Int, CalendarDate>> {
    if (calendarByGregorianKey.value.containsKey(startEpochDays)) {
        val calendarDate = calendarByGregorianKey.value.getValue(startEpochDays)
        return getDaysFlow(calendarDate, endEpochDays)
    } else {
        val date1 = LocalDate.fromEpochDays(startEpochDays)
        val date2 = LocalDate.fromEpochDays(endEpochDays)
        if (date1 < calendar.MIN_GREGORIAN_DATE_IN_HIJRI && date2 > calendar.MIN_GREGORIAN_DATE_IN_HIJRI) {

            val hijriLocalDate = hijriChronologies.calculateHijriDate(date2)
            val calendarDate = CalendarDate(date2, hijriLocalDate)
            return getDaysFlow(calendarDate, date1.toEpochDays())
        } else {
            val hijriLocalDate = hijriChronologies.calculateHijriDate(date1)
            val calendarDate = CalendarDate(date1, hijriLocalDate)
            return getDaysFlow(calendarDate, endEpochDays)
        }

    }
}

/**
 * @exception GregorianDateOutOfHijriRangeException If localDate is before 1882-11-11
 * @exception HijriDateNotFoundException The exception shouldn't occur, but if it did, there might be an error in the JSON file.
 */
internal fun DataManager.getDaysFlow(
    currentDate: CalendarDate,
    endEpochDays: Int,
): Flow<Pair<Int, CalendarDate>> = flow {

    var referenceHijriDay = currentDate.hijriLocalDate.dayOfMonth
    var referenceHijriMonth = currentDate.hijriLocalDate.month
    var referenceHijriYear = hijriChronologies.getValue(currentDate.hijriLocalDate.year)
    var daysRemainingInHijriMonth = referenceHijriYear.daysOfMonths[referenceHijriMonth.ordinal]
    val startEpochDays = currentDate.gregorianLocalDate.toEpochDays()

    var gregorianLocalDate: LocalDate
    var hijriLocalDate: HijriLocalDate

    if (startEpochDays <= endEpochDays) {

        for (day in startEpochDays..endEpochDays) {
            if (calendarByGregorianKey.value.containsKey(day)) continue

            // Update Hijri date if needed
            if (referenceHijriDay > daysRemainingInHijriMonth) {
                referenceHijriMonth = referenceHijriMonth.nextMonth()
                referenceHijriDay = calendar.FIRST_DAY

                if (referenceHijriMonth.number == 1 && isValidYear(referenceHijriYear.year + 1)) {
                    referenceHijriYear = hijriChronologies.getValue(referenceHijriYear.year + 1)
                } else if (referenceHijriMonth.number == 1) {
                    throw GregorianDateOutOfHijriRangeException(LocalDate.fromEpochDays(day))
                }

            }

            // Create and emit CalendarDate
            gregorianLocalDate = LocalDate.fromEpochDays(day)
            hijriLocalDate =
                HijriLocalDate(
                    referenceHijriYear.year,
                    referenceHijriMonth,
                    referenceHijriDay,
                    gregorianLocalDate.dayOfWeek.asHijri()
                )

            emit(day to CalendarDate(gregorianLocalDate, hijriLocalDate))

            ///////////////////
            referenceHijriDay++
        }
    } else {
        for (day in startEpochDays downTo endEpochDays) {
            if (calendarByGregorianKey.value.containsKey(day)) continue

            // Update Hijri date if needed
            if (referenceHijriDay < calendar.FIRST_DAY) {
                referenceHijriMonth = referenceHijriMonth.previousMonth()
                if (referenceHijriMonth.number == 12 && isValidYear(referenceHijriYear.year - 1)) {
                    referenceHijriYear =
                        hijriChronologies.getValue(currentDate.hijriLocalDate.year - 1)
                    daysRemainingInHijriMonth =
                        referenceHijriYear.daysOfMonths[referenceHijriMonth.ordinal]
                } else if (referenceHijriMonth.number == 12) {
                    throw GregorianDateOutOfHijriRangeException(LocalDate.fromEpochDays(day))
                }
                referenceHijriDay = daysRemainingInHijriMonth
            }

            // Create and emit CalendarDate
            gregorianLocalDate = LocalDate.fromEpochDays(day)
            hijriLocalDate = HijriLocalDate(
                referenceHijriYear.year,
                referenceHijriMonth,
                referenceHijriDay,
                gregorianLocalDate.dayOfWeek.asHijri()
            )
            emit(day to CalendarDate(gregorianLocalDate, hijriLocalDate))

            ///////////////////
            referenceHijriDay--
        }
    }
}

internal fun Map<Int, HijriYearInfo>.getDays(
    startEpochDays: Int,
    endEpochDays: Int,
): Map<Int, CalendarDate> {

    val localDate = LocalDate.fromEpochDays(startEpochDays - 1)
    val hijriLocalDate = calculateHijriDate(localDate)
    val calendarDate = CalendarDate(localDate, hijriLocalDate)
    return getDays(calendarDate, endEpochDays)
}


internal fun Map<Int, HijriYearInfo>.getDays(
    currentDate: CalendarDate,
    endEpochDays: Int,
): Map<Int, CalendarDate> {

    val result = mutableMapOf<Int, CalendarDate>()

    var referenceHijriDay = currentDate.hijriLocalDate.dayOfMonth
    var referenceHijriMonth = currentDate.hijriLocalDate.month
    var referenceHijriYear = this.getValue(currentDate.hijriLocalDate.year)
    var daysRemainingInHijriMonth = referenceHijriYear.daysOfMonths[referenceHijriMonth.ordinal]
    val startEpochDays = currentDate.gregorianLocalDate.toEpochDays()

    var gregorianLocalDate = currentDate.gregorianLocalDate
    var hijriLocalDate: HijriLocalDate

    if (startEpochDays <= endEpochDays) {

        for (day in startEpochDays..endEpochDays) {
            if (referenceHijriDay > daysRemainingInHijriMonth) {
                referenceHijriMonth = referenceHijriMonth.nextMonth()
                referenceHijriDay = FIRST_DAY
                if (referenceHijriMonth.number == 1 && isValidYear(referenceHijriYear.year + 1)) {
                    referenceHijriYear = this.getValue(currentDate.hijriLocalDate.year + 1)
                    daysRemainingInHijriMonth =
                        referenceHijriYear.daysOfMonths[referenceHijriMonth.ordinal]
                }
            }
            gregorianLocalDate = gregorianLocalDate.plus(ONE_DAY, DateTimeUnit.DAY)
            hijriLocalDate =
                HijriLocalDate(
                    referenceHijriYear.year,
                    referenceHijriMonth,
                    referenceHijriDay,
                    gregorianLocalDate.dayOfWeek.asHijri()
                )
            result[day] = CalendarDate(gregorianLocalDate, hijriLocalDate)

            ///////////////////
            referenceHijriDay++
        }
    } else {

        for (day in startEpochDays downTo endEpochDays) {
            if (referenceHijriDay < FIRST_DAY) {
                referenceHijriMonth = referenceHijriMonth.previousMonth()
                if (referenceHijriMonth.number == 12 && isValidYear(referenceHijriYear.year - 1)) {
                    referenceHijriYear = this.getValue(currentDate.hijriLocalDate.year - 1)
                    daysRemainingInHijriMonth =
                        referenceHijriYear.daysOfMonths[referenceHijriMonth.ordinal]
                }
                referenceHijriDay = daysRemainingInHijriMonth
            }
            gregorianLocalDate = gregorianLocalDate.minus(ONE_DAY, DateTimeUnit.DAY)
            hijriLocalDate =
                HijriLocalDate(
                    referenceHijriYear.year,
                    referenceHijriMonth,
                    referenceHijriDay,
                    gregorianLocalDate.dayOfWeek.asHijri()
                )
            result[day] = CalendarDate(gregorianLocalDate, hijriLocalDate)

            ///////////////////
            referenceHijriDay--
        }
    }

    return result
}