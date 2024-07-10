package lessons_analize_info

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlin.math.ceil
import kotlin.math.max

class LanguageProgressCalculator {

    fun calculateRangeFinalDates(
        startDate: LocalDate,
        currentLevel: ArabicLevel,
        targetLevel: ArabicLevel,
        condition: Condition,
    ): Pair<LocalDate, LocalDate> {

        val requiredHoursMin =
            targetLevel.requiredHoursFromZero().first - currentLevel.requiredHoursFromZero().first
        val requiredHoursMax =
            targetLevel.requiredHoursFromZero().second - currentLevel.requiredHoursFromZero().second

        val fullWeeksMin = (requiredHoursMin / condition.requiredHoursOfWeek).toInt()
        val fullWeeksMax = (requiredHoursMax / condition.requiredHoursOfWeek).toInt()

        val remainingHoursMin = (requiredHoursMin % condition.requiredHoursOfWeek).toInt()
        val remainingHoursMax = (requiredHoursMax % condition.requiredHoursOfWeek).toInt()
        val additionalDaysMin = ceil(remainingHoursMin / condition.lessonDuration.asHour()).toInt()
        val additionalDaysMax = ceil(remainingHoursMax / condition.lessonDuration.asHour()).toInt()
        val totalDaysMin = (fullWeeksMin * 7) + additionalDaysMin
        val totalDaysMax = (fullWeeksMax * 7) + additionalDaysMax

        return startDate.plus(totalDaysMin, DateTimeUnit.DAY) to startDate.plus(
            totalDaysMax,
            DateTimeUnit.DAY
        )
    }


    fun calculateConditions(
        currentLevel: ArabicLevel,
        targetLevel: ArabicLevel,
        startDate: LocalDate,
        finalDate: LocalDate
    ): List<Condition> {
        val totalDays = startDate.daysUntil(finalDate)
        val requiredHoursToTargetLevel =
            targetLevel.requiredHoursFromZero().second - currentLevel.requiredHoursFromZero().second

        val lessonPerWeekOptions = listOf(7, 6, 5, 4, 3)
        return lessonPerWeekOptions.map { lessonPerWeek ->
            calculateCondition(
                totalLessonHours = requiredHoursToTargetLevel,
                totalDays = totalDays,
                lessonPerWeek = lessonPerWeek
            )
        }
    }

    fun calculateRangeConditions(
        currentLevel: ArabicLevel,
        targetLevel: ArabicLevel,
        startDate: LocalDate,
        rangeOfFinalDate: Pair<LocalDate, LocalDate>
    ): List<Pair<DateRangeConditionResult, DateRangeConditionResult>> {
        val minTotalDays = startDate.daysUntil(rangeOfFinalDate.first)
        val maxTotalDays = startDate.daysUntil(rangeOfFinalDate.second)
        val minRequiredHoursToTargetLevel =
            targetLevel.requiredHoursFromZero().first - currentLevel.requiredHoursFromZero().first

        val maxRequiredHoursToTargetLevel =
            targetLevel.requiredHoursFromZero().second - currentLevel.requiredHoursFromZero().second

        val lessonPerWeekOptions = listOf(7, 6, 5, 4, 3)

        return lessonPerWeekOptions.map { lessonPerWeek ->

            Pair(

                DateRangeConditionResult(
                    startDate = startDate,
                    finalDate = rangeOfFinalDate.first,
                    minCondition = calculateCondition(
                        totalLessonHours = minRequiredHoursToTargetLevel,
                        totalDays = maxTotalDays,
                        lessonPerWeek = lessonPerWeek
                    ),
                    maxCondition = calculateCondition(
                        totalLessonHours = minRequiredHoursToTargetLevel,
                        totalDays = minTotalDays,
                        lessonPerWeek = lessonPerWeek
                    )
                ),
                DateRangeConditionResult(
                    startDate = startDate,
                    finalDate = rangeOfFinalDate.second,
                    minCondition = calculateCondition(
                        totalLessonHours = maxRequiredHoursToTargetLevel,
                        totalDays = maxTotalDays,
                        lessonPerWeek = lessonPerWeek
                    ),
                    maxCondition = calculateCondition(
                        totalLessonHours = maxRequiredHoursToTargetLevel,
                        totalDays = minTotalDays,
                        lessonPerWeek = lessonPerWeek
                    )
                )
            )
        }

//        return lessonPerWeekOptions.map { lessonPerWeek ->
//
//            Pair(
//                calculateCondition(
//                    totalLessonHours = minRequiredHoursToTargetLevel,
//                    totalDays = minTotalDays,
//                    lessonPerWeek = lessonPerWeek
//                ),
//                calculateCondition(
//                    totalLessonHours = maxRequiredHoursToTargetLevel,
//                    totalDays = maxTotalDays,
//                    lessonPerWeek = lessonPerWeek
//                )
//            )
//        }
    }

    private fun calculateCondition(
        totalLessonHours: Int,
        totalDays: Int,
        lessonPerWeek: Int
    ): Condition {
        val requiredWeeks = totalDays / 7
        val totalLessonDays = requiredWeeks * lessonPerWeek + totalDays % 7
        val lessonDuration =
            LessonDuration.createFromHours(totalLessonHours.toFloat() / totalLessonDays)
        return Condition(
            lessonDuration = lessonDuration,
            lessonPerWeek = lessonPerWeek
        )
    }

}