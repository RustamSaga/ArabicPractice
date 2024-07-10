package calendar.date_generation

import calendar.MAX_GREGORIAN_DATE_IN_HIJRI
import calendar.MAX_HIJRI_DATE
import calendar.MIN_GREGORIAN_DATE_IN_HIJRI
import calendar.MIN_HIJRI_DATE
import calendar.REFERENCE_DATES
import calendar.interfaces.DataManager
import calendar.model.CalendarDate
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import calendar.model.HijriYearInfo
import calendar.model.SearchableHijriDate
import calendar.model.asHijri
import calendar.model.number
import exceptions.GregorianDateOutOfHijriRangeException
import exceptions.HijriDateNotFoundException
import internal.goToBack
import internal.goToForth
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil


/**
 * @exception GregorianDateOutOfHijriRangeException If localDate is before 2015-10-14 or it is after 2174-11-25
 */
internal fun Map<Int, HijriYearInfo>.calculateHijriDate(localDate: LocalDate): HijriLocalDate {

    if (localDate > MAX_GREGORIAN_DATE_IN_HIJRI || localDate < MIN_GREGORIAN_DATE_IN_HIJRI)
        throw GregorianDateOutOfHijriRangeException(localDate)

    val referenceGregorianDate = REFERENCE_DATES.first
    val referenceHijriYear = REFERENCE_DATES.second.year
    val referenceHijriMonth = REFERENCE_DATES.second.month
    val referenceHijriDay = REFERENCE_DATES.second.dayOfMonth

    val days = localDate.daysUntil(referenceGregorianDate)
    val isForth = localDate > referenceGregorianDate

    val chronology = if (isForth) {
        this.filter { it.key >= referenceHijriYear }
    } else {
        this.filter { it.key <= referenceHijriYear }
            .entries
            .sortedByDescending { it.key }
            .associate { it.toPair() }
    }

    return if (isForth)
        goToForth(
            chronology = chronology,
            _days = -days,
            _referenceHijriDay = referenceHijriDay,
            _referenceHijriMonth = referenceHijriMonth,
            localDate = localDate
        )
    else
        goToBack(
            chronology,
            _days = days,
            _referenceHijriDay = referenceHijriDay,
            _referenceHijriMonth = referenceHijriMonth,
            localDate
        )
}

internal fun DataManager.calculateHijriDate(
    hijriDate: SearchableHijriDate
): CalendarDate {

    if (!isValidDateRange(hijriDate.year, hijriDate.month, hijriDate.dayOfMonth))
        throw HijriDateNotFoundException("Searched date: ${hijriDate.year}/${hijriDate.month}/${hijriDate.dayOfMonth}")

    val currentDate = today
    val currentHijriDate = currentDate.hijriLocalDate
    val isSameDate = currentHijriDate.year == hijriDate.year
            && currentHijriDate.month.number == hijriDate.month.number
            && currentHijriDate.dayOfMonth == hijriDate.dayOfMonth

    val localDateEpochDays = if (isSameDate) return currentDate else
        getEpochDays(hijriDate.year, hijriDate.month.number, hijriDate.dayOfMonth, currentDate)
    val targetLocalDate = LocalDate.fromEpochDays(localDateEpochDays)

    return CalendarDate(
        targetLocalDate,
        HijriLocalDate(
            hijriDate.year,
            hijriDate.month,
            hijriDate.dayOfMonth,
            targetLocalDate.dayOfWeek.asHijri()
        )
    )
}

internal fun isValidDateRange(year: Int, month: HijriMonth, dayOfMonth: Int): Boolean {
    return (year >= MIN_HIJRI_DATE.first && month.number >= MIN_HIJRI_DATE.second && dayOfMonth >= MIN_HIJRI_DATE.third) ||
            (year <= MAX_HIJRI_DATE.first && month.number <= MAX_HIJRI_DATE.second && dayOfMonth <= MAX_HIJRI_DATE.third)

}

internal fun DataManager.getEpochDays(
    targetYear: Int,
    targetMonthNumber: Int,
    targetDayOfMonth: Int,
    currentDate: CalendarDate
): Int {

    val currentHijriDate = currentDate.hijriLocalDate
    val gregorianEpochDays = currentDate.gregorianLocalDate.toEpochDays()

    return when {
        currentHijriDate.year < targetYear -> {
            gregorianEpochDays + currentYearIsLessThanTheTargetYear(
                targetYear, targetMonthNumber, targetDayOfMonth, currentHijriDate
            )
        }

        currentHijriDate.year == targetYear -> {
            gregorianEpochDays + currentYearIsEqualToTheTargetYear(
                targetYear, targetMonthNumber, targetDayOfMonth, currentHijriDate
            )
        }

        else -> {
            gregorianEpochDays + currentYearIsGreaterThanTheTargetYear(
                targetYear, targetMonthNumber, targetDayOfMonth, currentHijriDate
            )
        }
    }
}

internal fun DataManager.currentYearIsEqualToTheTargetYear(
    targetYear: Int,
    targetMonthNumber: Int,
    targetDayOfMonth: Int,
    currentHijriDate: HijriLocalDate
): Int {
    var countDays = 0
    val currentMonth = currentHijriDate.month
    val currentHijriYearInfo = hijriChronologies.getValue(targetYear)

    return when {
        currentMonth.number < targetMonthNumber -> {
            for (m in currentMonth.number..targetMonthNumber) {
                if (m < targetMonthNumber) {
                    countDays += currentHijriYearInfo.daysOfMonths[m - 1]
                    continue
                }
                for (d in calendar.FIRST_DAY..targetDayOfMonth) {
                    if (d < targetDayOfMonth) {
                        countDays++
                        continue
                    } else {
                        countDays++
                        break
                    }

                }
            }
            countDays
        }

        currentMonth.number == targetMonthNumber -> {
            countDays -= currentHijriDate.dayOfMonth - targetDayOfMonth
            countDays
        }

        else -> {
            for (m in currentMonth.number downTo targetMonthNumber) {
                if (m > targetMonthNumber) {
                    countDays -= currentHijriYearInfo.daysOfMonths[m - 1]
                    continue
                }
                countDays -= currentHijriDate.dayOfMonth +
                        currentHijriYearInfo.daysOfMonths[targetMonthNumber - 1] +
                        targetDayOfMonth
            }
            countDays
        }
    }


//    return countDays
}

internal fun DataManager.currentYearIsLessThanTheTargetYear(
    targetYear: Int,
    targetMonthNumber: Int,
    targetDayOfMonth: Int,
    currentHijriDate: HijriLocalDate
): Int {

    var countDays = 0

    // how many days to next year
    var currentHijriYearInfo = hijriChronologies.getValue(currentHijriDate.year)
    var daysUntilTheEndOfYear = 0

    for (m in currentHijriDate.month.number + 1..12) {
        daysUntilTheEndOfYear += currentHijriYearInfo.daysOfMonths[m - 1]
    }

    daysUntilTheEndOfYear += currentHijriYearInfo.daysOfMonths[currentHijriDate.month.ordinal] -
            currentHijriDate.dayOfMonth

    countDays += daysUntilTheEndOfYear
    val referenceHijriMonth: HijriMonth = HijriMonth.MUHARRAM


    // Skip years if there are more than 0 years between them

    val passYears = targetYear - currentHijriDate.year - 1
    if (passYears > 0) {
        for (year in currentHijriDate.year + 1..currentHijriDate.year + passYears) {
            val yearInfo = hijriChronologies.getValue(year)
            countDays += yearInfo.daysOfYear
        }
    }

    // days to target day
    currentHijriYearInfo = hijriChronologies.getValue(targetYear)
    for (m in referenceHijriMonth.number..targetMonthNumber) {
        if (m < targetMonthNumber) {
            countDays += currentHijriYearInfo.daysOfMonths[m - 1]
            continue
        }
        for (d in calendar.FIRST_DAY..targetDayOfMonth) {
            if (d < targetDayOfMonth) {
                countDays++
                continue
            } else {
                countDays++
                break
            }

        }
    }

    return countDays
}

internal fun DataManager.currentYearIsGreaterThanTheTargetYear(
    targetYear: Int,
    targetMonthNumber: Int,
    targetDayOfMonth: Int,
    currentHijriDate: HijriLocalDate
): Int {
    var countDays = 0

    // how many days to next year
    var currentHijriYearInfo = hijriChronologies.getValue(currentHijriDate.year)
    var daysUntilTheStartOfYear = 0

    for (m in currentHijriDate.month.number - 1 downTo 1) {
        daysUntilTheStartOfYear -= currentHijriYearInfo.daysOfMonths[m - 1]
    }

    daysUntilTheStartOfYear -= currentHijriDate.dayOfMonth

    countDays += daysUntilTheStartOfYear
    val referenceHijriMonth: HijriMonth = HijriMonth.ZU_AL_HIJJAH


    // Skip years if there are more than 0 years between them

    for (year in currentHijriDate.year - 1 downTo targetYear + 1) {
        val yearInfo = hijriChronologies.getValue(year)
        countDays -= yearInfo.daysOfYear
    }

    // days to target day
    currentHijriYearInfo = hijriChronologies.getValue(targetYear)
    for (m in 12 downTo targetMonthNumber) {
        if (m > targetMonthNumber) {
            countDays -= currentHijriYearInfo.daysOfMonths[m - 1]
            continue
        }
        val lastDays = currentHijriYearInfo.daysOfMonths[m - 1] - targetDayOfMonth
        countDays -= lastDays
    }

    return countDays
}