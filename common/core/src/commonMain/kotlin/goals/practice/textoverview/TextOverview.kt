package goals.practice.textoverview

import goals.Content
import goals.practice.Practice
import levels.Standard

class TextOverview(
    override val title: String,
    override val id: Int,
    override val content: Content<TextOverviewResult>
): Practice<TextOverviewResult> {
    val result = content.repetitions.values.first()

}