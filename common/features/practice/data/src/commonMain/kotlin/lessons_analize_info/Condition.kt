package lessons_analize_info

data class Condition(
    val lessonDuration: LessonDuration,
    val lessonPerWeek: Int
) {

    val requiredHoursOfWeek = lessonDuration.asHour() * lessonPerWeek

}