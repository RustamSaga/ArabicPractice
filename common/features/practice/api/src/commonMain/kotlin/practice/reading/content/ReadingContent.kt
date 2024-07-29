package practice.reading.content

import content.contract_models.FullContent
import practice.reading.result.ReadingResult

interface ReadingContent : FullContent<ReadingResult> {
    val wordsNumber: Int
}