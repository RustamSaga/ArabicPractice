package calendar.model

data class HijriLocalDate(
    val year: Int,
    val month: HijriMonth,
    val dayOfMonth: Int,
    val dayOfWeek: HijriDayOfWeek
) {
    override fun toString(): String {
        return "$year-${month.number}-$dayOfMonth-$dayOfWeek"
    }

    internal val key = keyOf(year, month, dayOfMonth)

}

data class SearchableHijriDate(
    val year: Int,
    val month: HijriMonth,
    val dayOfMonth: Int,
) {
    internal val key = keyOf(year, month, dayOfMonth)
}

private fun keyOf(year: Int, month: HijriMonth, dayOfMonth: Int): Int {
    return "$year${month.number.adaptedNumber()}${dayOfMonth.adaptedNumber()}".toInt()
}

internal fun Int.adaptedNumber(): String {
    return if (this > 9) this.toString() else "0$this"
}
