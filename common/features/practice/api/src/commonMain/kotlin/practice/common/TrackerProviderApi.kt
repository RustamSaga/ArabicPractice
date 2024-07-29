package practice.common

import practice.reading.options.TrainingOptions
import practice.reading.tracker.ReadingTracker

/**
 * Main class.
 */
interface TrackerProviderApi {
//    suspend fun provideListeningTracker(): ListeningTracker
//    suspend fun provideSpeakingTracker(): SpeakingTracker
//    suspend fun provideWritingTracker(): WritingTracker
//    suspend fun provideVocabularyTracker(): VocabularyTracker
//    suspend fun provideTextOverflowTracker(): TextOverflowTracker
    suspend fun provideReadingTracker(
        contentText: String,
        options: TrainingOptions
    ): ReadingTracker
}