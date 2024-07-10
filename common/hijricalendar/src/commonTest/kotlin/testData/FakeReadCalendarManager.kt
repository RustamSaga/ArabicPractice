package testData

import calendar.interfaces.ReadCalendarManager
import calendar.model.CalendarDate
import calendar.model.SearchableHijriDate
import calendarOfOneHijriWeek
import exceptions.GregorianDateOutOfHijriRangeException
import exceptions.HijriDateNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class FakeReadCalendarManager(
    override val today: CalendarDate
): ReadCalendarManager {
    override suspend fun getDate(localDate: LocalDate): CalendarDate {
        return try {
            calendarOfOneHijriWeek.first {
                it.gregorianLocalDate == localDate
            }
        } catch (e: GregorianDateOutOfHijriRangeException) {
            throw GregorianDateOutOfHijriRangeException(localDate)
        }
    }

    override suspend fun getDate(hijriDate: SearchableHijriDate): CalendarDate {
        return try {
            calendarOfOneHijriWeek.first {
                it.hijriLocalDate.year == hijriDate.year &&
                        it.hijriLocalDate.month == hijriDate.month &&
                        it.hijriLocalDate.dayOfMonth == hijriDate.dayOfMonth
            }
        } catch (e: HijriDateNotFoundException) {
            throw e
        }
    }

    override fun getDatesRange(
        fromLocalDate: LocalDate,
        toLocalDate: LocalDate
    ): Flow<Pair<Int, CalendarDate>> {
        return calendarOfOneHijriWeek.filter {
            it.gregorianLocalDate in fromLocalDate..toLocalDate
        }.asFlow().map {
            Pair(it.gregorianLocalDate.toEpochDays(), it)
        }
    }

    override fun getDatesRange(
        fromHijriDate: SearchableHijriDate,
        toHijriDate: SearchableHijriDate
    ): Flow<Pair<Int, CalendarDate>> {
        return calendarOfOneHijriWeek.filter {
            it.hijriLocalDate.year in fromHijriDate.year..toHijriDate.year &&
                    it.hijriLocalDate.month in fromHijriDate.month..toHijriDate.month &&
                    it.hijriLocalDate.dayOfMonth in fromHijriDate.dayOfMonth..toHijriDate.dayOfMonth
        }.asFlow().map {
            Pair(it.gregorianLocalDate.toEpochDays(), it)
        }
    }
}