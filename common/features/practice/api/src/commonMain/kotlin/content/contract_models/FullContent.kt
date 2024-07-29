package content.contract_models

import content.search.ContentLevel
import practice.common.result.MainResult

interface FullContent<T: MainResult>: MainContent {
    val id: Int
    val level: ContentLevel
    val description: String
    val text: String
    val repeatNumber: Int
    val result: List<T>
    val notCompletedResults: List<T>
}