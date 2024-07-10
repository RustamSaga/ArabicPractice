package data.testdata

import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import kotlinx.datetime.LocalDate
val calendarOfOneHijriWeek1 = mapOf(
    LocalDate(2024, 5, 19).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 19),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 11, HijriDayOfWeek.AL_AHAD)
    ),
    LocalDate(2024, 5, 20).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 20),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 12, HijriDayOfWeek.AL_ITHNAYN)
    ),
    LocalDate(2024, 5, 21).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 21),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 13, HijriDayOfWeek.ATH_THULATHA)
    ),
    LocalDate(2024, 5, 22).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 22),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 14, HijriDayOfWeek.AL_ARBIA)
    ),
    LocalDate(2024, 5, 23).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 23),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 15, HijriDayOfWeek.AL_KHAMIS)
    ),
    LocalDate(2024, 5, 24).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 24),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 16, HijriDayOfWeek.AL_JUMAH)
    ),
    LocalDate(2024, 5, 25).toEpochDays() to
    CalendarDate(
        LocalDate(2024, 5, 25),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 17, HijriDayOfWeek.AS_SABT)
    )
)

val calendarOfOneHijriWeek2 = listOf(
    CalendarDate(
        LocalDate(2024, 5, 26),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 18, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        LocalDate(2024, 5, 27),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 19, HijriDayOfWeek.AL_ITHNAYN)
    ),
    CalendarDate(
        LocalDate(2024, 5, 28),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 20, HijriDayOfWeek.ATH_THULATHA)
    ),
    CalendarDate(
        LocalDate(2024, 5, 29),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 21, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        LocalDate(2024, 5, 30),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 22, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        LocalDate(2024, 5, 31),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 23, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        LocalDate(2024, 6, 1),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 24, HijriDayOfWeek.AS_SABT)
    ),
)
val calendarOfOneHijriWeek_index_plus_1 = listOf(
    CalendarDate(
        LocalDate(2024, 6, 2),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 25, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        LocalDate(2024, 6, 3),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 26, HijriDayOfWeek.AL_ITHNAYN)
    ),
    CalendarDate(
        LocalDate(2024, 6, 4),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 27, HijriDayOfWeek.ATH_THULATHA)
    ),
    CalendarDate(
        LocalDate(2024, 6, 5),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 28, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        LocalDate(2024, 6, 6),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 29, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        LocalDate(2024, 6, 7),
        HijriLocalDate(1445, HijriMonth.ZU_AL_HIJJAH, 1, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        LocalDate(2024, 6, 8),
        HijriLocalDate(1445, HijriMonth.ZU_AL_HIJJAH, 2, HijriDayOfWeek.AS_SABT)
    ),
)

//////////////////////////////////////////////////////////////////////////////////
val calendarOfOneHijriWeek = listOf(
    CalendarDate(
        LocalDate(2024, 5, 29),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 21, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        LocalDate(2024, 5, 30),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 22, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        LocalDate(2024, 5, 31),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 23, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        LocalDate(2024, 6, 1),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 24, HijriDayOfWeek.AS_SABT)
    ),
    CalendarDate(
        LocalDate(2024, 6, 2),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 25, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        LocalDate(2024, 6, 3),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 26, HijriDayOfWeek.AL_ITHNAYN)
    ),
    CalendarDate(
        LocalDate(2024, 6, 4),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 27, HijriDayOfWeek.ATH_THULATHA)
    ),
)

val calendarOfOneHijriWeekInTheYearAndNextYear = listOf(
    CalendarDate(
        LocalDate(2024, 7, 5),
        HijriLocalDate(1445, HijriMonth.ZU_AL_HIJJAH, 29, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        LocalDate(2024, 7, 6),
        HijriLocalDate(1445, HijriMonth.ZU_AL_HIJJAH, 30, HijriDayOfWeek.AS_SABT)
    ),
    CalendarDate(
        LocalDate(2024, 7, 7),
        HijriLocalDate(1446, HijriMonth.MUHARRAM, 1, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        LocalDate(2024, 7, 8),
        HijriLocalDate(1446, HijriMonth.MUHARRAM, 2, HijriDayOfWeek.AL_ITHNAYN)
    ),
    CalendarDate(
        LocalDate(2024, 7, 9),
        HijriLocalDate(1446, HijriMonth.MUHARRAM, 3, HijriDayOfWeek.ATH_THULATHA)
    ),
    CalendarDate(
        LocalDate(2024, 7, 10),
        HijriLocalDate(1446, HijriMonth.MUHARRAM, 4, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        LocalDate(2024, 7, 11),
        HijriLocalDate(1446, HijriMonth.MUHARRAM, 5, HijriDayOfWeek.AL_KHAMIS)
    )
)
val calendarOfOneHijriWeekInTwoMonth = listOf(
    CalendarDate(
        LocalDate(2024, 5, 7),
        HijriLocalDate(1445, HijriMonth.SHAWWAL, 28, HijriDayOfWeek.ATH_THULATHA)
    ),
    CalendarDate(
        LocalDate(2024, 5, 8),
        HijriLocalDate(1445, HijriMonth.SHAWWAL, 29, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        LocalDate(2024, 5, 9),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 1, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        LocalDate(2024, 5, 10),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 2, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        LocalDate(2024, 5, 11),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 3, HijriDayOfWeek.AS_SABT)
    ),
    CalendarDate(
        LocalDate(2024, 5, 12),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 4, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        LocalDate(2024, 5, 13),
        HijriLocalDate(1445, HijriMonth.ZU_AL_QAADAH, 5, HijriDayOfWeek.AL_ITHNAYN)
    )
)

val calendarOfOneHijriWeekInTheYearAndPreviousYear = listOf(
    CalendarDate(
        LocalDate(2024, 7, 14),
        HijriLocalDate(1444, HijriMonth.ZU_AL_HIJJAH, 26, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        LocalDate(2024, 7, 15),
        HijriLocalDate(1444, HijriMonth.ZU_AL_HIJJAH, 27, HijriDayOfWeek.AL_ITHNAYN)
    ),
    CalendarDate(
        LocalDate(2024, 7, 16),
        HijriLocalDate(1444, HijriMonth.ZU_AL_HIJJAH, 28, HijriDayOfWeek.ATH_THULATHA)
    ),
    CalendarDate(
        LocalDate(2024, 7, 17),
        HijriLocalDate(1444, HijriMonth.ZU_AL_HIJJAH, 29, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        LocalDate(2024, 7, 18),
        HijriLocalDate(1444, HijriMonth.ZU_AL_HIJJAH, 30, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        LocalDate(2024, 7, 19),
        HijriLocalDate(1445, HijriMonth.MUHARRAM, 1, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        LocalDate(2024, 7, 20),
        HijriLocalDate(1445, HijriMonth.MUHARRAM, 2, HijriDayOfWeek.AS_SABT)
    )
)