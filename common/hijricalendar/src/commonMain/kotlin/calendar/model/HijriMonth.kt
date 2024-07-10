package calendar.model

enum class HijriMonth(name: String) {

    MUHARRAM(internal.MUHARRAM),
    SAFAR(internal.SAFAR),
    RABI_AL_AWWAL(internal.RABI_AL_AWWAL),
    RABI_ATH_THANI(internal.RABI_ATH_THANI),
    JUMADA_AL_ULA(internal.JUMADA_AL_ULA),
    JUMADA_AL_AHIRAH(internal.JUMADA_AL_AHIRAH),
    RAJAB(internal.RAJAB),
    SHAABAN(internal.SHAABAN),
    RAMADAN(internal.RAMADAN),
    SHAWWAL(internal.SHAWWAL),
    ZU_AL_QAADAH(internal.ZU_AL_QAADAH),
    ZU_AL_HIJJAH(internal.ZU_AL_HIJJAH);

}

public val HijriMonth.number: Int get() = ordinal + 1

fun Int.nextNumberOfMonth() = (this % 12) + 1
fun Int.previousNumberOfMonth() = if (this == 1) 12 else this - 1

fun HijriMonth.nextMonth() = HijriMonth(this.number.nextNumberOfMonth())
fun HijriMonth.previousMonth() = HijriMonth(this.number.previousNumberOfMonth())

public fun HijriMonth(number: Int): HijriMonth {
    require(number in 1..12)
    return HijriMonth.entries[number - 1]
}