package calendar


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import calendar.interfaces.CalendarManager
import calendar.interfaces.CurrentGregorianDateProvider
import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import calendar.model.SearchableHijriDate
import datatest.calendarOfOneHijriWeek
import exceptions.GregorianDateOutOfHijriRangeException
import exceptions.MonthDaysOutOfRangeException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import utils.SharedFileReader
import utils.dateToday
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AndroidCalendarManagerTest {
    private lateinit var manager: CalendarManager

    private val testCurrentGregorianDateProvider = object : CurrentGregorianDateProvider {
        override val today: LocalDate
            get() = LocalDate(2024, 6, 1) // hijri 1445-11-24
        override val dateTime: LocalDateTime
            get() = LocalDateTime(2024, 6, 1, 20, 24)

    }
    @BeforeTest
    fun init() = runBlocking {
        val appContext: Context = ApplicationProvider.getApplicationContext()
        manager = CalendarManagerImpl(testCurrentGregorianDateProvider, SharedFileReader(appContext), "raw/hijrichronology.json")
    }

    @Test
    fun theFirstRetrievalOfTheCurrentDate() = runBlocking {
        var isContainsCurrentDate =
            manager.calendarByGregorianKey.value.containsKey(testCurrentGregorianDateProvider.today.toEpochDays())
        val containsPair = manager.calendarsByHijriKey
        assertEquals(false, isContainsCurrentDate)
        assertEquals(emptyMap<Int, CalendarDate>(), containsPair)

        val gregorianCurrentDate = testCurrentGregorianDateProvider.today
        val hijriLocalDate = HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH,24, HijriDayOfWeek.AS_SABT)
        val expected = CalendarDate(gregorianCurrentDate,hijriLocalDate)
        val actual = manager.today
        isContainsCurrentDate =
            manager.calendarByGregorianKey.value.containsKey(testCurrentGregorianDateProvider.today.toEpochDays())
        assertEquals(true, isContainsCurrentDate)
        assertEquals(expected, actual)
    }

    @Test
    fun gettingTheDateFromLocalDateSucceeded() = runBlocking {
        var isContainsAnyDate = manager.calendarByGregorianKey.value.isNotEmpty()
        val pairOfCalendars = manager.calendarsByHijriKey
        assertEquals(emptyMap<Int, CalendarDate>(), pairOfCalendars)
        assertEquals(false, isContainsAnyDate)
        val expected = CalendarDate(
            LocalDate(2030, 12, 1),
            HijriLocalDate(1452, HijriMonth.SHAABAN, 5, HijriDayOfWeek.AL_AHAD)
        )
        val actualDate = manager.getDate(LocalDate(2030, 12, 1))
        assertEquals(expected, actualDate)
        isContainsAnyDate = manager.calendarByGregorianKey.value.isNotEmpty()
        assertEquals(true, isContainsAnyDate)

        val localDateFromHijri = manager.calendarsByHijriKey.getValue(expected.hijriLocalDate.key)
        assertEquals(expected, localDateFromHijri)
    }

    @Test
    @Throws(GregorianDateOutOfHijriRangeException::class)
    fun testForDateBelowTheMaximumBoundary(): Unit = runBlocking {
        assertFails {
            manager.getDate(LocalDate(2200, 12, 1))
        }
    }

    @Test
    @Throws(GregorianDateOutOfHijriRangeException::class)
    fun testForDateBelowTheMinimumBoundary(): Unit = runBlocking {
        assertFails {
            manager.getDate(LocalDate(2000, 12, 1))
        }
    }

    @Test
    fun gettingTheRangeOfDates_AllDatesWereSuccessfullyProcessed() = runBlocking {
        val expected = calendarOfOneHijriWeek
        val flow = manager
            .getDatesRange(LocalDate(2024, 5, 19), LocalDate(2024, 5, 25))
        val actual = flow.toList().toMap()
        assertEquals(expected, actual)
        assertEquals(expected.size, manager.calendarsByHijriKey.size)


    }

    @Test
    fun gettingTheRangeOfDatesFromEnd_notAllDatesWereSuccessfullyProcessed() =
        runBlocking {
            val start = LocalDate(2174, 11, 20)
            val end = LocalDate(2174, 12, 30)
            val expected = 6
            val flow = manager
                .getDatesRange(start, end)
            val actual = flow.toList().size
            flow.toList().forEach {
                println(it)
            }
            assertEquals(expected, actual)
            assertEquals(expected, manager.calendarsByHijriKey.size)
        }

    @Test
    fun gettingTheRangeOfDatesFromStart_notAllDatesWereSuccessfullyProcessed() =
        runBlocking {
            val start = LocalDate(2015, 10, 11)
            val end = LocalDate(2015, 10, 19)
            val expected = 6
            val flow = manager.getDatesRange(start, end)
            val actual = flow.toList().size

            assertEquals(expected, actual)
            assertEquals(expected, manager.calendarsByHijriKey.size)
        }

    @Test
    fun theFirstGenerationCalendarCoveringSpanOfThreeYearsHasBeenSuccessfullyCompleted() =
        runBlocking {
            val currentDate = dateToday
            val daysOfCurrentYear = LocalDate(currentDate.year + 1, 1, 1).toEpochDays() -
                    LocalDate(currentDate.year, 1, 1).toEpochDays()
            val daysOfNextYear = LocalDate(currentDate.year + 2, 1, 1).toEpochDays() -
                    LocalDate(currentDate.year + 1, 1, 1).toEpochDays()
            val daysOfPreviousYear = LocalDate(currentDate.year, 1, 1).toEpochDays() -
                    LocalDate(currentDate.year - 1, 1, 1).toEpochDays()

            val firstExpectedSize = daysOfPreviousYear + daysOfCurrentYear + daysOfNextYear

            val dates = manager.calendarByGregorianKey
            assertEquals(emptyMap<Int, CalendarDate>(), dates.value)
            manager.getFirstThreeYearsFromPrevious()

            assertEquals(firstExpectedSize, dates.value.size)
        }

    @Test
    fun gettingTheDateBySearchableHijriDateIfTheTargetYearIsGreaterThanTheCurrentYearSucceeded() =
        runBlocking {
            var isContainsAnyDate = manager.calendarByGregorianKey.value.isNotEmpty()
            val calendarsByHijriKey = manager.calendarsByHijriKey
            assertEquals(emptyMap<Int, CalendarDate>(), calendarsByHijriKey)
            assertEquals(false, isContainsAnyDate)

            val expected = CalendarDate(
                LocalDate(2025, 10, 7),
                HijriLocalDate(1447, HijriMonth.RABI_ATH_THANI, 15, HijriDayOfWeek.ATH_THULATHA)
            )
            val actualDate = manager.getDate(
                SearchableHijriDate(1447, HijriMonth.RABI_ATH_THANI, 15)
            )
            assertEquals(expected, actualDate)
            isContainsAnyDate = manager.calendarByGregorianKey.value.isNotEmpty()
            assertEquals(true, isContainsAnyDate)

            val localDateFromHijri =
                manager.calendarsByHijriKey.getValue(expected.hijriLocalDate.key)
            assertEquals(expected, localDateFromHijri)
        }


    @Test
    fun gettingTheTargetDateWereTheTargetYearIsLessThanTheCurrentYearWasSuccessful() =
        runBlocking {
            var isContainsAnyDate = manager.calendarByGregorianKey.value.isNotEmpty()
            val calendarsByHijriKey = manager.calendarsByHijriKey
            assertEquals(emptyMap<Int, CalendarDate>(), calendarsByHijriKey)
            assertEquals(false, isContainsAnyDate)

            val expected = CalendarDate(
                LocalDate(2016, 7, 15),
                HijriLocalDate(1437, HijriMonth.SHAWWAL, 10, HijriDayOfWeek.AL_JUMAH)
            )
            val actualDate = manager.getDate(
                SearchableHijriDate(1437, HijriMonth.SHAWWAL, 10)
            )
            assertEquals(expected, actualDate)
            isContainsAnyDate = manager.calendarByGregorianKey.value.isNotEmpty()
            assertEquals(true, isContainsAnyDate)

            val localDateFromHijri =
                manager.calendarsByHijriKey.getValue(expected.hijriLocalDate.key)
            assertEquals(expected, localDateFromHijri)
        }

    @Test
    fun gettingTheTargetForTheSameYearAndMonthAsTheCurrentDate_butBeforeTheCurrentDay_wasSuccessful() =
        runBlocking {
            val expectedLessDate = CalendarDate(
                LocalDate(2024, 5, 20),
                HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 12, HijriDayOfWeek.AL_ITHNAYN)
            )
            val actualLessDate = manager.getDate(
                SearchableHijriDate(1445, HijriMonth.ZU_AL_QAADAH, 12)
            )
            assertEquals(expectedLessDate, actualLessDate)
            assertEquals(
                expectedLessDate,
                manager.calendarByGregorianKey.value.getValue(expectedLessDate.gregorianLocalDate.toEpochDays())
            )
            assertEquals(
                expectedLessDate,
                manager.calendarsByHijriKey.getValue(expectedLessDate.hijriLocalDate.key)
            )
        }

    @Test
    fun gettingTargetForSameYearAndMonthAsTheCurrentDate_butAfterTheCurrentDay() =
        runBlocking {
            val expectedGreaterDate = CalendarDate(
                LocalDate(2024, 6, 6),
                HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 29, HijriDayOfWeek.AL_KHAMIS)
            )
            val actualGreaterDate = manager.getDate(
                SearchableHijriDate(1445, HijriMonth.ZU_AL_QAADAH, 29)
            )

            assertEquals(expectedGreaterDate, actualGreaterDate)
            assertEquals(
                expectedGreaterDate,
                manager.calendarByGregorianKey.value.getValue(expectedGreaterDate.gregorianLocalDate.toEpochDays())
            )
            assertEquals(
                expectedGreaterDate,
                manager.calendarsByHijriKey.getValue(expectedGreaterDate.hijriLocalDate.key)
            )
        }

    @Test
    fun targetDateSameCurrentDate() = runBlocking {

        val expectedEqualDate = manager.today
        val searchableHijriDate = SearchableHijriDate(
            expectedEqualDate.hijriLocalDate.year,
            expectedEqualDate.hijriLocalDate.month,
            expectedEqualDate.hijriLocalDate.dayOfMonth
        )
        val actualEqualDate = manager.getDate(searchableHijriDate)
        assertEquals(expectedEqualDate, actualEqualDate)
        assertEquals(
            expectedEqualDate,
            manager.calendarByGregorianKey.value.getValue(expectedEqualDate.gregorianLocalDate.toEpochDays())
        )
        assertEquals(
            expectedEqualDate,
            manager.calendarsByHijriKey.getValue(expectedEqualDate.hijriLocalDate.key)
        )
    }

    @Test
    @Throws(MonthDaysOutOfRangeException::class)
    fun invalidTargetDate(): Unit = runBlocking {
        assertFails {
            manager.getDate(SearchableHijriDate(1444, HijriMonth.ZU_AL_QAADAH, 33))
        }
    }
}