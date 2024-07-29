package practice.fake.model

import content.search.ContentLevel
import practice.reading.content.ReadingContent
import practice.reading.result.ReadingResult

class FaceReadingContent(
    override val id: Int,
    override val level: ContentLevel = ContentLevel.A0,
    override val description: String = "",
    override val text: String,
    override val repeatNumber: Int = 0,
    override val result: List<ReadingResult> = emptyList(),
    override val notCompletedResults: List<ReadingResult> = emptyList(),
    override val wordsNumber: Int
) : ReadingContent