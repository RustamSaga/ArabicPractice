package practice.reading.result

import kotlinx.datetime.LocalDateTime
import practice.common.WordsTimeMillis

data class ReadingTrackerResult(
    val dateTime: LocalDateTime,
    val result: WordsTimeMillis
)