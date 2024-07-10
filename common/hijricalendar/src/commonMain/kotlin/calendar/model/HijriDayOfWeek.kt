package calendar.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

enum class HijriDayOfWeek(name: String) {
    AL_AHAD(internal.AL_AHAD),
    AL_ITHNAYN(internal.AL_ITHNAYN),
    ATH_THULATHA(internal.ATH_THULATHA),
    AL_ARBIA(internal.AL_ARBIA),
    AL_KHAMIS(internal.AL_KHAMIS),
    AL_JUMAH(internal.AL_JUMAH),
    AS_SABT(internal.AS_SABT);
}

public val HijriDayOfWeek.isoHijriDayNumber: Int get() = ordinal + 1

public fun HijriDayOfWeek(isoDayNumber: Int): HijriDayOfWeek {
    require(isoDayNumber in 1..7) { "Expected ISO day-of-week number in 1..7, got $isoDayNumber" }
    return HijriDayOfWeek.entries[isoDayNumber - 1]
}

public fun HijriDayOfWeek.asGregorian(): DayOfWeek = when (this) {
    HijriDayOfWeek.AL_AHAD -> DayOfWeek.SUNDAY
    HijriDayOfWeek.AL_ITHNAYN -> DayOfWeek.MONDAY
    HijriDayOfWeek.ATH_THULATHA -> DayOfWeek.TUESDAY
    HijriDayOfWeek.AL_ARBIA -> DayOfWeek.WEDNESDAY
    HijriDayOfWeek.AL_KHAMIS -> DayOfWeek.THURSDAY
    HijriDayOfWeek.AL_JUMAH -> DayOfWeek.FRIDAY
    HijriDayOfWeek.AS_SABT -> DayOfWeek.SATURDAY
}

fun DayOfWeek.asHijri(): HijriDayOfWeek = when (this) {
    DayOfWeek.MONDAY -> HijriDayOfWeek.AL_ITHNAYN
    DayOfWeek.TUESDAY -> HijriDayOfWeek.ATH_THULATHA
    DayOfWeek.WEDNESDAY -> HijriDayOfWeek.AL_ARBIA
    DayOfWeek.THURSDAY -> HijriDayOfWeek.AL_KHAMIS
    DayOfWeek.FRIDAY -> HijriDayOfWeek.AL_JUMAH
    DayOfWeek.SATURDAY -> HijriDayOfWeek.AS_SABT
    DayOfWeek.SUNDAY -> HijriDayOfWeek.AL_AHAD
    else -> throw IllegalArgumentException("Unknown day of week: $this")
}

public val DayOfWeek.isoHijriDayNumber: Int get() =
    if (isoDayNumber == 7) 1 else isoDayNumber - 1

public val HijriDayOfWeek.isoGregorianDayNumber: Int get() =
    if (isoHijriDayNumber == 1) 7 else isoHijriDayNumber + 1