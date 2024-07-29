package common

import exceptions.NoElementException
import kotlinx.coroutines.coroutineScope
import platform.PlatformConfiguration
import practice.common.TrackerProviderApi
import practice.reading.options.TrainingOptions
import practice.reading.tracker.ReadingTracker
import practice.reading.ReadingTrackerImpl
import utils.CurrentDateProvider
import utils.Logger
import time_tracker.interfaces.TimeTracker
import utils.speaktotext.createSpeakToText
import utils.toListWords

class TrackerProvider(
    private val platformConfiguration: PlatformConfiguration,
    private val timeTracker: TimeTracker,
    private val currentDateProvider: CurrentDateProvider,
    private val logger: Logger
) : TrackerProviderApi {
    override suspend fun provideReadingTracker(
        contentText: String,
        options: TrainingOptions
    ): ReadingTracker {
        if (contentText.isEmpty()) throw NoElementException("Content is empty")

        return coroutineScope {

            val splitText = contentText.toListWords() // TODO список должен состоять только из слов. Нужно переписать сплит
            val speakToText = try {
                createSpeakToText(platformConfiguration, this)
            } catch (e: Exception) {
                logger.error("${e.message}. Error getting SpeakToText: Failed to unpack the model")
                throw e
            }

            return@coroutineScope ReadingTrackerImpl(
                content = splitText,
                coroutineScope = this,
                option = options,
                currentDateProvider = currentDateProvider,
                timeTracker = timeTracker,
                speakToText = speakToText
            )
        }

    }

//    override suspend fun provideListeningTracker(): ListeningTracker {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun provideSpeakingTracker(): SpeakingTracker {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun provideWritingTracker(): WritingTracker {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun provideVocabularyTracker(): VocabularyTracker {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun provideTextOverflowTracker(): TextOverflowTracker {
//        TODO("Not yet implemented")
//    }
}