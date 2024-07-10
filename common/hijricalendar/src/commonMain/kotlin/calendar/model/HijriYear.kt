package calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class HijriYear(
    val year: Int,
    val daysOfMonths: List<Int>
) {
    fun toHijriYearInfo(daysOfYear: Int): HijriYearInfo {
        return HijriYearInfo(
            year = year,
            daysOfYear = daysOfYear,
            daysOfMonths = daysOfMonths
        )
    }
}

data class HijriYearInfo(
    val year: Int,
    val daysOfYear: Int,
    val daysOfMonths: List<Int>
)