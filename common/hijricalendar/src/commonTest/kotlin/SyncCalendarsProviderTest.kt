import calendar.CalendarMode
import calendar.SyncCalendarsProvider
import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import calendar.model.SearchableHijriDate
import exceptions.GregorianDateOutOfHijriRangeException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import model.Location
import testData.FakeReadCalendarManager
import testData.FakeSunsetTimeProvider
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SyncCalendarsProviderTest {

    lateinit var syncCalendarsProvider: SyncCalendarsProvider

    private val today = CalendarDate(
        LocalDate(2024, 6, 1),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 24, HijriDayOfWeek.AS_SABT)
    )
    private val sunsetTime = LocalTime(19, 0)
    private val sunsetTimeProvider = FakeSunsetTimeProvider(
        timeNow = LocalTime(17, 0),
        sunsetTime = sunsetTime
    )
    private val calendarManager = FakeReadCalendarManager(today)

    private val searchableHijriDate = SearchableHijriDate(1445, HijriMonth.ZU_AL_QAADAH, 24)


    @BeforeTest
    fun init() {
        syncCalendarsProvider = SyncCalendarsProvider(
            readCalendarManager = calendarManager,
            sunsetTimeProvider = sunsetTimeProvider
        )
    }

    @Test
    fun gregorianCalendarMode_gettingCurrentDay() = runTest {
        val actual = syncCalendarsProvider.today()
        val expected = CalendarDate(
            LocalDate(2024, 6, 1),
            HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 24, HijriDayOfWeek.AS_SABT)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun hijriCalendarMode_gettingCurrentDayWithoutLocation() = runTest {
        syncCalendarsProvider.updateCalendarMode(CalendarMode.HIJRI)
        val actual = syncCalendarsProvider.today(null)
        val expected = CalendarDate(
            LocalDate(2024, 6, 1),
            HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 24, HijriDayOfWeek.AS_SABT)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun hijriCalendarMode_gettingCurrentDayWithLocation() = runTest {
        syncCalendarsProvider.updateCalendarMode(CalendarMode.HIJRI)
        val actual = syncCalendarsProvider.today(Location(0f, 0f))
        val expected = today
        assertEquals(expected, actual)
    }

    @Test
    fun hijriCalendarMode_gettingCurrentDayWithLocationAndSunsetTime() = runTest {
        syncCalendarsProvider.updateCalendarMode(CalendarMode.HIJRI)
        sunsetTimeProvider.timeNow = sunsetTime
        val actual = syncCalendarsProvider.today(Location(0f, 0f))
        val expected = CalendarDate(
            LocalDate(2024, 6, 2),
            HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 25, HijriDayOfWeek.AL_AHAD)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun gettingCalendarDayByLocalDateWasSuccessful() = runTest {
        val expected = CalendarDate(
            LocalDate(2024, 5, 30),
            HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 22, HijriDayOfWeek.AL_KHAMIS)
        )
        val actual = syncCalendarsProvider.getDate(expected.gregorianLocalDate)

        assertEquals(expected, actual)
    }

    @Test
    @Throws(GregorianDateOutOfHijriRangeException::class)
    fun gettingCalendarDayByLocalDateWasFail() = runTest {
        val expected = CalendarDate(
            LocalDate(2024, 5, 30),
            HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 22, HijriDayOfWeek.AL_KHAMIS)
        )

        assertFails {
            syncCalendarsProvider.getDate(expected.gregorianLocalDate.minus(4, DateTimeUnit.YEAR))
        }
    }

    @Test
    fun gettingCalendarDayByHijriDateWasSuccessful() = runTest {
        val expected = CalendarDate(
            LocalDate(2024, 5, 30),
            HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 22, HijriDayOfWeek.AL_KHAMIS)
        )
        val actual =
            syncCalendarsProvider.getDate(SearchableHijriDate(1445, HijriMonth.ZU_AL_QAADAH, 22))

        assertEquals(expected, actual)
    }

    @Test
    @Throws(GregorianDateOutOfHijriRangeException::class)
    fun gettingCalendarDayByHijriDateWasFail() = runTest {
        assertFails {
            syncCalendarsProvider.getDate(SearchableHijriDate(1440, HijriMonth.MUHARRAM, 1))
        }
    }

    @Test
    fun gettingDateRangeByLocalDateWasSuccessful() = runTest {
        val expected = listOf(
            Pair(
                LocalDate(2024, 5, 29).toEpochDays(),
                CalendarDate(
                    LocalDate(2024, 5, 29),
                    HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 21, HijriDayOfWeek.AL_ARBIA)
                )
            ),
            Pair(
                LocalDate(2024, 5, 30).toEpochDays(),
                CalendarDate(
                    LocalDate(2024, 5, 30),
                    HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 22, HijriDayOfWeek.AL_KHAMIS)
                )
            ),
            Pair(
                LocalDate(2024, 5, 31).toEpochDays(),
                CalendarDate(
                    LocalDate(2024, 5, 31),
                    HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 23, HijriDayOfWeek.AL_JUMAH)
                ),
            )
        )

        val actual = syncCalendarsProvider.getDateRange(
            fromLocalDate = LocalDate(2024, 5, 29),
            toLocalDate = LocalDate(2024, 5, 31)
        ).toList()
        assertEquals(expected, actual)

        // empty list
        assertEquals(
            emptyList(),
            syncCalendarsProvider
                .getDateRange(LocalDate(2000, 1, 1), LocalDate(2010, 1, 1))
                .toList()
        )
    }

    @Test
    fun gettingDateRangeByHijriDateWasSuccessful() = runTest {
        val expected = listOf(
            Pair(
                LocalDate(2024, 5, 31).toEpochDays(),
                CalendarDate(
                    LocalDate(2024, 5, 31),
                    HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 23, HijriDayOfWeek.AL_JUMAH)
                )
            ),
            Pair(
                LocalDate(2024, 6, 1).toEpochDays(),
                CalendarDate(
                    LocalDate(2024, 6, 1),
                    HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 24, HijriDayOfWeek.AS_SABT)
                )
            ),
            Pair(
                LocalDate(2024, 6, 2).toEpochDays(),
                CalendarDate(
                    LocalDate(2024, 6, 2),
                    HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 25, HijriDayOfWeek.AL_AHAD)
                )
            )
        )

        val actual = syncCalendarsProvider.getDateRange(
            fromHijriDate = SearchableHijriDate(1445, HijriMonth.ZU_AL_QAADAH, 23),
            toHijriDate = SearchableHijriDate(1445, HijriMonth.ZU_AL_QAADAH, 25)
        ).toList()
        assertEquals(expected, actual)
    }
}