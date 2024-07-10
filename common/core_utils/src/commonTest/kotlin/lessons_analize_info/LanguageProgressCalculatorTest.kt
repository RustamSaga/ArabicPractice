package lessons_analize_info

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.until
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageProgressCalculatorTest {

    lateinit var lpCalculator: LanguageProgressCalculator

    @BeforeTest
    fun init() {
        lpCalculator = LanguageProgressCalculator()
    }

    @Test
    fun calculationRangeOfFinalDatesWasSuccessful() {
        val targetLevels = listOf(
            ArabicLevel.A1 to (LocalDate(2024, 6, 6) to LocalDate(2024, 6, 10)),
            ArabicLevel.A2 to (LocalDate(2024, 7, 16) to LocalDate(2024, 8, 9)),
            ArabicLevel.B1 to (LocalDate(2024, 9, 18) to LocalDate(2024, 11, 13)),
            ArabicLevel.B2 to (LocalDate(2024, 11, 29) to LocalDate(2025, 2, 25)),
            ArabicLevel.C1 to (LocalDate(2025, 2, 17) to LocalDate(2025, 6, 25)),
            ArabicLevel.C2 to (LocalDate(2025, 6, 17) to LocalDate(2025, 12, 2))
        )


        for ((target, finalDate) in targetLevels) {
            val actual = lpCalculator.calculateRangeFinalDates(
                startDate = LocalDate(2024, 5, 1),
                currentLevel = ArabicLevel.A0,
                targetLevel = target,
                condition = Condition(
                    lessonDuration = LessonDuration(2, 30),
                    lessonPerWeek = 7
                )
            )

            assertEquals(finalDate, actual)

        }
    }

    @Test
    fun calculationAConditionsFromStartAndFinalDatesWasSuccessful() {

        val expected: List<Condition> = listOf(
            Condition(LessonDuration(5, 37), 7),
            Condition(LessonDuration(6, 32), 6),
            Condition(LessonDuration(7, 48), 5),
            Condition(LessonDuration(9, 41), 4),
            Condition(LessonDuration(12, 46), 3),
        )


        val actual: List<Condition> = lpCalculator.calculateConditions(
            currentLevel = ArabicLevel.A2,
            targetLevel = ArabicLevel.C2,
            startDate = LocalDate(2024, 6, 1),
            finalDate = LocalDate(2025, 1, 1)
        )

        assertEquals(expected, actual)

    }
    @Test
    fun calculationARangeConditionsFromStartAndFinalDatesWasSuccessful() {

        val expected: List<Condition> = listOf(
            Condition(LessonDuration(5, 37), 7),
            Condition(LessonDuration(6, 32), 6),
            Condition(LessonDuration(7, 48), 5),
            Condition(LessonDuration(9, 41), 4),
            Condition(LessonDuration(12, 46), 3),
        )


        val actual = lpCalculator.calculateRangeConditions(
            currentLevel = ArabicLevel.A2,
            targetLevel = ArabicLevel.C2,
            startDate = LocalDate(2024, 6, 1),
            rangeOfFinalDate = LocalDate(2025, 1, 1) to LocalDate(2025, 2, 1)
        )

        println(LocalDate(2024, 6, 1).daysUntil(LocalDate(2025, 1, 1)))
        println(LocalDate(2024, 6, 1).daysUntil(LocalDate(2025, 2, 1)))

        actual.forEach {
            println(
                """
                    First conditions of date range from ${it.first.startDate} to ${it.first.finalDate} is:
                        minimum - ${it.first.minCondition.lessonDuration.hour}:${it.first.minCondition.lessonDuration.min} / ${it.first.minCondition.lessonPerWeek} days in week
                        maximum - ${it.first.maxCondition.lessonDuration.hour}:${it.first.maxCondition.lessonDuration.min} / ${it.first.maxCondition.lessonPerWeek} days in week
                    
                    Second condition of date range from ${it.second.startDate} to ${it.second.finalDate} is:
                        minimum - ${it.second.minCondition.lessonDuration.hour}:${it.second.minCondition.lessonDuration.min} / ${it.second.minCondition.lessonPerWeek} days in week
                        maximum - ${it.second.maxCondition.lessonDuration.hour}:${it.second.maxCondition.lessonDuration.min} / ${it.second.maxCondition.lessonPerWeek} days in week
                """.trimIndent()
            )
        }

    }


}