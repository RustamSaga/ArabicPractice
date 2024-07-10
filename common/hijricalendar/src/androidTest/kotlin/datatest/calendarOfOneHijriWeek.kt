package datatest

import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import kotlinx.datetime.LocalDate

val calendarOfOneHijriWeek = mapOf(
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