package lessons_analize_info

import kotlin.math.ceil

class LessonDuration(hour: Int, min: Int) {
    val hour: Int
    val min: Int

    init {
        require(min in 0..59) { throw IllegalArgumentException("The value of 'min' must be between 0 and 59.") }
        require(hour in 1..16) { throw IllegalArgumentException("The value of 'hour' must be between 1 and 16.") }
        this.hour = hour
        this.min = min
    }

    fun asHour(): Float = hour + min.toFloat() / 60

    override fun toString(): String = "LessonDuration: ${hour}h:${min}min" // 20h:20min

    override fun equals(other: Any?): Boolean {
        return this.hour == (other as LessonDuration).hour && this.min == other.min
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + min
        return result
    }

    companion object {
        fun createFromHours(hours: Float): LessonDuration {
            val resultHours = hours.toInt()
            val resultMin = ceil((hours - resultHours) * 60).toInt()
            return LessonDuration(resultHours, resultMin)
        }
    }
}