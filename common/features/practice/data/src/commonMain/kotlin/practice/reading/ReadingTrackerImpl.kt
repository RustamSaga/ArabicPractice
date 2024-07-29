package practice.reading

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.TimeMillisData
import practice.common.WordsTimeMillis
import practice.reading.options.MicrophonePermissionManager
import practice.reading.options.TrainingOptions
import practice.reading.result.ReadingTrackerResult
import practice.reading.tracker.ReadingTracker
import utils.CurrentDateProvider
import utils.Logger
import time_tracker.interfaces.TimeTracker
import utils.getPlatformLogger
import utils.speaktotext.SpeakToText

/**
 * Implementation of the [ReadingTracker] interface for tracking reading progress.
 * Manages the reading session, including timing, speech-to-text processing, and word tracking.
 *
 * @param content List of words to be tracked during the reading session. It is important that the list of words consists solely of words, without commas or other punctuation marks.
 * @param option Configuration options for the training session, including microphone permissions and timer settings.
 * @param currentDateProvider Provides the current date for tracking purposes.
 * @param timeTracker Manages timing and duration for the reading session.
 * @param speakToText Interface for speech-to-text services used for listening to spoken words.
 */
class ReadingTrackerImpl(
    override val content: List<String>,
    override val option: TrainingOptions,
    private val coroutineScope: CoroutineScope,
    private val currentDateProvider: CurrentDateProvider,
    private val timeTracker: TimeTracker,
    private val speakToText: SpeakToText,
    private val logger: Logger = getPlatformLogger()
) : ReadingTracker {

    private var trackingJob: Job? = null
    private var observeFinishJob: Job? = null
    private var listeningJob: Job? = null

    init {
        timeTracker.setLimit(option.readingSpeedTimer)
    }

    private var result: ReadingTrackerResult? = null

    private val readWords: MutableList<WordStatus> =
        content.map { WordStatus(false, it) }.toMutableList()

    private var currentIndex = 0

    private val finishState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mutableWordsFromMic: MutableSharedFlow<String> = MutableSharedFlow(0, 60)

    /**
     *  If you create a coroutine for this flow, the coroutine will not stop because
     *  it contains a StateFlow. To stop it, you need to programmatically call its cancellation.
     */
    override val wordsFromMic: SharedFlow<String>
        get() = mutableWordsFromMic


    override val stopwatchCurrentTime: StateFlow<TimeMillisData>
        get() = timeTracker.stopwatchCurrentTime
    override val timerCurrentTime: StateFlow<TimeMillisData>
        get() = timeTracker.timerCurrentTime
    override val isFinished: StateFlow<Boolean>
        get() = finishState

    /**
     * A [MutableStateFlow] that emits updates when all words have been read.
     */
    private val _allWordsReadStateFlow = MutableStateFlow(false)

    override fun start() {
        if (timeTracker.isStopwatchRunning()) {
            logger.log("Time tracker already started")
            return
        }

        coroutineScope.launch {
            finishState.value = false

            trackingJob = launch(CoroutineName("ReadingTrackerImpl: trackingJob")) {
                timeTracker.start()
            }

            option.micPermission.ifGranted {
                listeningJob = launch(
                    Dispatchers.IO + CoroutineName("ReadingTrackerImpl: listeningJob")
                ) {
                    speakToText.startListening()
                    launch {
                        speakToText.formattedWords.collect { formattedWord ->
                            if (markWordAsRead(formattedWord, currentIndex)) {
                                mutableWordsFromMic.emit(formattedWord)
                            }
                        }
                    }
                    launch {
                        _allWordsReadStateFlow.collect { allRead ->
                            if (allRead) finish()
                        }
                    }
                }
            }
        }
    }

    /**
     * Starts the reading tracking session. Initializes the time tracker, begins listening for words,
     * and observes the state flow to determine when all words have been read.
     *
     * **Important:** This function will not automatically stop the stopwatch or end the tracking session on its own.
     * To avoid blocking the coroutine that calls this method, ensure that it is invoked within a separate coroutine
     * or scope that can handle asynchronous operations.
     *
     * @param stopwatchCurrentTime Callback for updating the stopwatch time.
     * @param timerCurrentTime Callback for updating the timer.
     * @param currentWord Callback for handling the current spoken word, if the user is using the microphone.
     * @param ifFinished Callback for handling the completion status.
     *
     * @throws IllegalStateException if the time tracker is already started.
     */
    override fun start(
        stopwatchCurrentTime: (formattedTime: String, timeMillis: Long) -> Unit,
        timerCurrentTime: (formattedTime: String, timeMillis: Long) -> Unit,
        currentWord: (word: String) -> Unit,
        ifFinished: () -> Unit
    ) {
        if (timeTracker.isStopwatchRunning()) {
            logger.log("Time tracker already started")
            return
        }
        coroutineScope.launch {
            finishState.value = false

            trackingJob = launch(CoroutineName("ReadingTrackerImpl: trackingJob")) {
                startTracking(stopwatchCurrentTime, timerCurrentTime)
            }

            observeFinishJob =
                launch(Dispatchers.IO + CoroutineName("ReadingTrackerImpl: observeFinishJob")) {
                    finishState.collect { finished ->
                        if (finished) {
                            ifFinished()
                        }
                    }
                }

            option.micPermission.ifGranted {
                listeningJob = launch(
                    Dispatchers.IO + CoroutineName("ReadingTrackerImpl: listeningJob")
                ) {
                    speakToText.startListening()
                    launch {
                        speakToText.formattedWords.collect { formattedWord ->
                            if (markWordAsRead(formattedWord, currentIndex)) {
                                currentWord(formattedWord)
                                mutableWordsFromMic.emit(formattedWord)
                            }
                        }
                    }
                    launch {
                        _allWordsReadStateFlow.collect { allRead ->
                            if (allRead) finish()
                        }
                    }
                }
            }
        }
    }

    override suspend fun pause() {
        timeTracker.pause()
        speakToText.stopListening()
        logger.log("The tracker paused")
    }

    override suspend fun resume() {
        timeTracker.resume()
        speakToText.startListening()
        logger.log("The tracker resumed")
    }

    private suspend fun startTracking(
        stopwatchCurrentTime: (formattedTime: String, timeMillis: Long) -> Unit,
        timerCurrentTime: (formattedTime: String, timeMillis: Long) -> Unit
    ) {
        logger.log("The tracker started")
        timeTracker.start(
            stopwatchCurrentTime = {
                stopwatchCurrentTime(it.formattedTime, it.timeMillis)
            },
            timerCurrentTime = {
                timerCurrentTime(it.formattedTime, it.timeMillis)
            },
        )
    }

    /**
     * Ends the reading session, stops the time tracker, and updates the result with the total number of words
     * and time taken.
     *
     * Logs the completion result with the reading statistics.
     */
    override suspend fun finish() {
        withContext(Dispatchers.IO) {
            timeTracker.stop()
            speakToText.stopListening()
            finishState.emit(true)
            result = ReadingTrackerResult(
                result = WordsTimeMillis(
                    wordsNumber = if (option.micPermission::class == MicrophonePermissionManager.Enable::class)
                        readWords.filter { it.isRead }.size
                        else content.size,
                    timeMillis = timeTracker.stopwatchCurrentTime.value.timeMillis.toInt()
                ),
                dateTime = currentDateProvider.today
            )
            trackingJob?.cancel()
            observeFinishJob?.cancel()
            listeningJob?.cancel()
            trackingJob = null
            observeFinishJob = null
            listeningJob = null
            logger.log("Finished reading with result: $result")
            logger.log("Finished reading with stopwatch result: ${timeTracker.stopwatchCurrentTime.value}")
            logger.log("Finished reading with timer result: ${timeTracker.timerCurrentTime.value}")
        }
    }

    /**
     * Marks a specific word as read by its index. Checks if the word is correct and updates its status.
     *
     * @param word The word to mark as read.
     * @param index The index of the word in the list.
     * @return `Boolean` indicating whether the word was successfully marked as read.
     */
    private fun markWordAsRead(word: String, index: Int): Boolean =
        try {
            val wordStatus = readWords[index]
            if (wordStatus.word == word) {
                readWords[index] = wordStatus.copy(isRead = true)
                checkAllWordsRead()
                currentIndex++ // Increment the index for tracking progress
                true
            } else {
                false
            }
        } catch (e: Exception) {
            logger.log("${e.message}. Error marking word as read")
            false
        }


    /**
     * Returns the elapsed time of the reading session.
     *
     * @return `Long` representing the elapsed time in milliseconds.
     */
    override fun getElapsedTime(): Long = timeTracker.stopwatchCurrentTime.value.timeMillis

    /**
     * Returns the result of the reading session, including the total number of words and time taken.
     *
     * @return [ReadingTrackerResult]? containing the result details or `null` if not yet set.
     */
    override fun getResult(): ReadingTrackerResult? = result
    override fun stopwatchResult(): TimeMillisData {
        return timeTracker.stopwatchCurrentTime.value
    }

    override fun timerResult(): TimeMillisData {
        return timeTracker.timerCurrentTime.value
    }

    /**
     * Checks if all words in the list have been marked as read and updates the state flow.
     *
     * Updates [_allWordsReadStateFlow].value to `true` if all words are read, which can then be collected
     * to perform any necessary actions (e.g., finishing the session).
     */
    private fun checkAllWordsRead() {
        if (readWords.last().isRead) {
            _allWordsReadStateFlow.value = true
        }
    }
}