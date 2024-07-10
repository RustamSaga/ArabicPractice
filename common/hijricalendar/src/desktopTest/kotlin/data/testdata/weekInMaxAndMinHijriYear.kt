package data.testdata

import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import kotlinx.datetime.LocalDate

val penultimateWeekOfTheLastHijriYear = listOf(
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 13),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 18, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 14),
        hijriLocalDate = HijriLocalDate(
            1600,
            HijriMonth.ZU_AL_HIJJAH,
            19,
            HijriDayOfWeek.AL_ITHNAYN
        )
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 15),
        hijriLocalDate = HijriLocalDate(
            1600,
            HijriMonth.ZU_AL_HIJJAH,
            20,
            HijriDayOfWeek.ATH_THULATHA
        )
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 16),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 21, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 17),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 22, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 18),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 23, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 19),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 24, HijriDayOfWeek.AS_SABT)
    )
)

val lastWeekInLastHijriYear = listOf(
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 20),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 25, HijriDayOfWeek.AL_AHAD)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 21),
        hijriLocalDate = HijriLocalDate(
            1600,
            HijriMonth.ZU_AL_HIJJAH,
            26,
            HijriDayOfWeek.AL_ITHNAYN
        )
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 22),
        hijriLocalDate = HijriLocalDate(
            1600,
            HijriMonth.ZU_AL_HIJJAH,
            27,
            HijriDayOfWeek.ATH_THULATHA
        )
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 23),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 28, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 24),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 29, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2174, 11, 25),
        hijriLocalDate = HijriLocalDate(1600, HijriMonth.ZU_AL_HIJJAH, 30, HijriDayOfWeek.AL_JUMAH)
    )
)

val firstWeekInMinHijriYear = listOf(
    CalendarDate(
        gregorianLocalDate = LocalDate(2015, 10, 14),
        hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 1, HijriDayOfWeek.AL_ARBIA)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2015, 10, 15),
        hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 2, HijriDayOfWeek.AL_KHAMIS)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2015, 10, 16),
        hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 3, HijriDayOfWeek.AL_JUMAH)
    ),
    CalendarDate(
        gregorianLocalDate = LocalDate(2015, 10, 17),
        hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 4, HijriDayOfWeek.AS_SABT)
    )
)

val secondWeekInMinHijriYear = listOf(
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 18), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 5, HijriDayOfWeek.AL_AHAD)),
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 19), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 6, HijriDayOfWeek.AL_ITHNAYN)),
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 20), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 7, HijriDayOfWeek.ATH_THULATHA)),
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 21), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 8, HijriDayOfWeek.AL_ARBIA)),
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 22), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 9, HijriDayOfWeek.AL_KHAMIS)),
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 23), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 10, HijriDayOfWeek.AL_JUMAH)),
    CalendarDate(gregorianLocalDate = LocalDate(2015, 10, 24), hijriLocalDate = HijriLocalDate(1437, HijriMonth.MUHARRAM, 11, HijriDayOfWeek.AS_SABT))
)