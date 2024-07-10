package lessons_analize_info

import kotlinx.datetime.LocalDate

data class DateRangeConditionResult(
    val startDate: LocalDate,
    val finalDate: LocalDate,
    val minCondition: Condition,
    val maxCondition: Condition
)