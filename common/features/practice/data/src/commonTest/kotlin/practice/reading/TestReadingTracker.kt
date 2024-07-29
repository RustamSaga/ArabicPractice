package practice.reading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.TimeMillisData
import practice.common.WordsTimeMillis
import practice.fake.FakeContentProvider
import practice.fake.FakeCurrentDateProvider
import practice.fake.FakeLogger
import practice.fake.FakeMicSpeakToText
import practice.reading.content.ReadingContent
import practice.reading.options.MicrophonePermissionManager
import practice.reading.options.ReadingSpeedTimer
import practice.reading.options.TrainingOptions
import practice.reading.result.ReadingTrackerResult
import stopwatch.Stopwatch
import time.ElapsedTimeCalculator
import time.TimestampProvider
import time_tracker.TimeTrackerImpl
import timer.Timer
import utils.toListWords
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestReadingTracker {

    @Test
    fun `Create and configure Reading Tracker with stopwatch enabled and mic disabled`() =
        runBlocking {
            println("Jobs count: ${this.coroutineContext.job.children.count()}")

            // 0 - set mocks
            var stopwatchCurrentTimeCount = 0
            var timerCurrentTimeCount = 0
            var currentWord = 0
            var ifFinished = 0

            // 1 - choose a content
            val contentProvider = FakeContentProvider()
            val content = contentProvider
                .getFullContentById(id = 1, ReadingContent::class)

            // 2 - Configure param
            val splitTextContent = content.text.toListWords()
            val readingSpeedTimer =
                ReadingSpeedTimer.HighNativeSpeedTimer(content.wordsNumber)
            val noTimerMicDisableOption =
                TrainingOptions(readingSpeedTimer, MicrophonePermissionManager.Disable)
            val elapsedTimeCalculator = ElapsedTimeCalculator(TimestampProvider)

            // 3 - Create tracker
            val tracker = ReadingTrackerImpl(
                content = splitTextContent,
                coroutineScope = this,
                option = noTimerMicDisableOption,
                currentDateProvider = FakeCurrentDateProvider(),
                timeTracker = TimeTrackerImpl(
                    stopwatch = Stopwatch(
                        elapsedTimeCalculator = elapsedTimeCalculator,
                        scope = this
                    ),
                    timer = Timer(
                        elapsedTimeCalculator = elapsedTimeCalculator,
                        scope = this
                    )
                ),
                speakToText = FakeMicSpeakToText(splitTextContent),
                logger = FakeLogger()
            )


            // 3 - Запуск тренинга
            val job: Job = launch {

                tracker.start(
                    stopwatchCurrentTime = { formattedTime, timeMillis ->
                        println("stopwatch time: $formattedTime")
                        stopwatchCurrentTimeCount++
                    },
                    timerCurrentTime = { formattedTime, timeMillis ->
                        println("timer time: $formattedTime")
                        timerCurrentTimeCount++
                    },
                    currentWord = {
                        println("current word: $it")
                        currentWord++
                    },
                    ifFinished = {
                        ifFinished++
                        println("THE PROCESS HAS FINISHED. Count: $ifFinished")
                    }
                )
            }

            // 4 - in process
            delay(5000)
            println("In process. User is reading content")

            println("Jobs count: ${this.coroutineContext.job.children.count()}")
            this.coroutineContext.job.children.forEach {
                println("Jobs count: $it")
            }
            println()

            // 5 - finish
            println("Calling finish")
            tracker.finish()
            this.coroutineContext.job.children.forEach {
                println("Jobs count: $it")
            }
            println("Is finish state true = ${tracker.isFinished.value}")
            job.cancel()
            println("Jobs count: ${this.coroutineContext.job.children.count()}")


            // 6 - получение результата
            val trackerResult: ReadingTrackerResult? = tracker.getResult()
            val stopwatchResult: TimeMillisData = tracker.stopwatchResult()
            val timerResult: TimeMillisData = tracker.timerResult()
            val milliseconds: Long = tracker.getElapsedTime()

            trackerResult?.result.shouldBe(wordsNumber = 5, timeMillisRange = 4900..5100)
            stopwatchResult shouldBe 4900..5100
            timerResult shouldBe -3100..-2900
            ifFinished shouldBe 1

            milliseconds shouldBe 4900..5100
            timerCurrentTimeCount not 0
            stopwatchCurrentTimeCount not 0
            currentWord shouldBe 0

        }


    @Test
    fun `Create and configure Reading Tracker with stopwatch enabled and mic enabled`() =
        runBlocking {

            // 0 - init tests values
            val testSecondLimit = 5000L
            val words: MutableList<String> = mutableListOf()
            var ifFinished = 0

            // 1 - choose a content
            val contentProvider = FakeContentProvider()
            val content = contentProvider
                .getFullContentById(id = 3, ReadingContent::class)

            // 2 - Configure param
            val splitTextContent = content.text.toListWords()
            val readingSpeedTimer =
                ReadingSpeedTimer.HighNativeSpeedTimer(content.wordsNumber)
            val noTimerMicDisableOption =
                TrainingOptions(readingSpeedTimer, MicrophonePermissionManager.Enable)
            val elapsedTimeCalculator = ElapsedTimeCalculator(TimestampProvider)

            // 3 - Create tracker
            val tracker = ReadingTrackerImpl(
                content = splitTextContent,
                coroutineScope = this,
                option = noTimerMicDisableOption,
                currentDateProvider = FakeCurrentDateProvider(),
                timeTracker = TimeTrackerImpl(
                    stopwatch = Stopwatch(
                        elapsedTimeCalculator = elapsedTimeCalculator,
                        scope = this
                    ),
                    timer = Timer(
                        elapsedTimeCalculator = elapsedTimeCalculator,
                        scope = this
                    )
                ),
                speakToText = FakeMicSpeakToText(splitTextContent),
                logger = FakeLogger()
            )


            // 3 - Запуск тренинга
            val runningJob = launch {
                tracker.start(
                    stopwatchCurrentTime = { formattedTime, timeMillis ->
                        println("stopwatch: $formattedTime")
                    },
                    timerCurrentTime = { formattedTime, timeMillis ->
                        println("timer: $formattedTime")
                    },
                    currentWord = {
                        println("current word: $it")
                        words.add(it)
                    },
                    ifFinished = {
                        ifFinished++
                        println("the process has finished. Count: $ifFinished")
                    }
                )
            }

            // The created coroutine will not stop because it contains a StateFlow.
            // To stop it, you need to programmatically call its cancellation.
            val microphoneJob = launch {
                tracker.wordsFromMic.collect {
                    println("This words in test block code. From tracker wordsFromMic collect. Current word: $it")
                }
            }
            val finishStateJob = launch {
                tracker.isFinished.collect {
                    println("Finish state = $it")
                }
            }

            delay(testSecondLimit)
            if (!runningJob.isActive) {
                microphoneJob.cancel(); finishStateJob.cancel()
            }
            val result = tracker.getResult()
            result?.let {
                it.result.timeMillis.toLong() shouldBe 3500..3600
            }
            words.size shouldBe 7
            tracker.timerResult().timeMillis shouldBe -1600..1500
            tracker.stopwatchResult().timeMillis shouldBe 3500..3600

        }

    @Test
    fun `Test Start with mic permission Pause Resume and Forced Termination`() =
        runBlocking {

            val wordsRead = mutableListOf<String>()

            val content = FakeContentProvider().getFullContentById(2, ReadingContent::class)
            val tracker = getTrackerByConfig(
                coroutineScope = this,
                content = content,
                timerType = ReadingSpeedTimer.HighNativeSpeedTimer(content.wordsNumber),
                micPermission = MicrophonePermissionManager.Enable
            )


            // 3 - Запуск тренинга
            launch {
                tracker.start(
                    stopwatchCurrentTime = { formattedTime, timeMillis ->
                        println("stopwatch: $formattedTime")
                    },
                    timerCurrentTime = { formattedTime, timeMillis ->
                        println("timer: $formattedTime")
                    },
                    currentWord = {
                        wordsRead.add(it)
                        println("current word: $it")
                    },
                    ifFinished = {
                        println("The process has finished")
                    }
                )
            }


            delay(1100)
            tracker.pause()
            delay(5000)
            tracker.resume()
            delay(1100)
            tracker.finish()

            wordsRead.size shouldBe 4
            println("wordsRead: $wordsRead")
            val result = tracker.getResult()

            result!!.result.timeMillis.toLong() shouldBe 2000..2200
            tracker.timerResult().timeMillis shouldBe 800..1000
            tracker.stopwatchResult().timeMillis shouldBe 2000..2200

        }


    @Test
    fun `Test start without params and enabled everything`() = runBlocking {
        val wordsRead = mutableListOf<String>()
        var timerMillisecond = 0L
        var stopwatchMillisecond = 0L
        val isFinished = MutableStateFlow(false)

        val content = FakeContentProvider().getFullContentById(2, ReadingContent::class)
        val tracker = getTrackerByConfig(
            coroutineScope = this,
            content = content,
            timerType = ReadingSpeedTimer.HighNativeSpeedTimer(content.wordsNumber),
            micPermission = MicrophonePermissionManager.Enable
        )

        val trackerJob = launch {
            tracker.start()
            launch {
                tracker.isFinished.collect {
                    println("Reading is finished = $it")
                    isFinished.value = it
                }
            }
            launch {
                tracker.stopwatchCurrentTime.collect {
                    println("stopwatch time: ${it.formattedTime}")
                    stopwatchMillisecond = it.timeMillis
                }
            }
            launch {
                tracker.timerCurrentTime.collect {
                    println("timer time: ${it.formattedTime}")
                    timerMillisecond = it.timeMillis
                }
            }
            launch {
                tracker.wordsFromMic.collect {
                    println("current word: $it")
                    wordsRead.add(it)
                }
            }
        }

        launch {
            isFinished.collect {
                if (it) {
                    cancel()
                    trackerJob.cancel()
                }
            }
        }

        trackerJob.join()
        assertEquals(isFinished.value, true)
        // 3 sec for timer - but speed read of user = 500 millis 1 word = 500*10 = 5 sec
        timerMillisecond shouldBe -2100..-1900
        stopwatchMillisecond shouldBe 4900..5100
        wordsRead containsWords listOf(
            "هذه",
            "برنامج",
            "يحتوي",
            "على",
            "دروس",
            "نظرية",
            "وعملية",
            "هذه",
            "جملة",
            "تجربية"
        )

    }

    private infix fun TimeMillisData.shouldBe(timeMillisRange: IntRange) {
        assertTrue {
            this.timeMillis in timeMillisRange
        }
    }

    private infix fun Long.shouldBe(intRange: IntRange) {
        assertTrue { this in intRange }
    }

    private fun WordsTimeMillis?.shouldBe(wordsNumber: Int, timeMillisRange: IntRange) {
        assertEquals(wordsNumber, this?.wordsNumber)
        assertTrue {
            this?.timeMillis in timeMillisRange
        }
    }

    private infix fun Int.shouldBe(i: Int) {
        assertEquals(i, this)
    }

    private infix fun Int.not(i: Int) {
        assertTrue { this != i }
    }

    private infix fun <E> List<E>.containsWords(expect: List<E>) {
        assertEquals(expect, this)
    }

    private fun getTrackerByConfig(
        coroutineScope: CoroutineScope,
        content: ReadingContent,
        timerType: ReadingSpeedTimer,
        micPermission: MicrophonePermissionManager
    ): ReadingTrackerImpl {

        // 2 - Configure param
        val splitTextContent = content.text.toListWords()


        val option = TrainingOptions(timerType, micPermission)
        val elapsedTimeCalculator = ElapsedTimeCalculator(TimestampProvider)

        // 3 - Create tracker
        return ReadingTrackerImpl(
            content = splitTextContent,
            coroutineScope = coroutineScope,
            option = option,
            currentDateProvider = FakeCurrentDateProvider(),
            timeTracker = TimeTrackerImpl(
                stopwatch = Stopwatch(
                    elapsedTimeCalculator = elapsedTimeCalculator,
                    scope = coroutineScope
                ),
                timer = Timer(
                    elapsedTimeCalculator = elapsedTimeCalculator,
                    scope = coroutineScope
                )
            ),
            speakToText = FakeMicSpeakToText(splitTextContent),
            logger = FakeLogger()
        )
    }
}