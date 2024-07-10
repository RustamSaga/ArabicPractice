package goals.practice.textoverview

import utils.WorkTime
import goals.Measurement

data class TextOverviewResult(
    override val id: Int,
    val unknownWordCount: Int = 0,
//    val completedDate: DateTime? = null,
    val completed: Boolean = false,
    val workTime: WorkTime
): Measurement {
}