package practice.common.result

import kotlinx.datetime.LocalDateTime

interface PracticeResult<T>: MainResult {
    val id: Int
    val contentId: Int
    val repeatNumber: Int
    val date: LocalDateTime
    val result: T
}