import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import kotlinx.datetime.LocalDate

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