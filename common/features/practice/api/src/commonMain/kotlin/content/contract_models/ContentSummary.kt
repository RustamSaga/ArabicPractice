package content.contract_models

import content.search.ContentLevel
//import practice.common.status.ListeningStatus
//import practice.common.status.ReadingStatus
//import practice.common.status.SpeakingStatus
//import practice.common.status.VocabularyStatus
//import practice.common.status.WritingStatus

interface ContentSummary {
    val id: Int
    val level: ContentLevel
    val description: String

//    val readingStatus: ReadingStatus
//    val speakingStatus: SpeakingStatus
//    val vocabularyStatus: VocabularyStatus
//    val writingStatus: WritingStatus
//    val listeningStatus: ListeningStatus
}