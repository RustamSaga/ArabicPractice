package stopwatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import model.TimeMillisData
import state.StopwatchState
import time.ElapsedTimeCalculator
import time.FakeTimestampProvider
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestStopwatch {

    private val coroutineDispatcher = StandardTestDispatcher()
    private val coroutineScope = CoroutineScope(SupervisorJob() + coroutineDispatcher)
    private val fakeTimestampProvider = FakeTimestampProvider()
    private val elapsedTimeCalculator = ElapsedTimeCalculator(fakeTimestampProvider)

    @Test
    fun `Initially the stopwatch status is stopped and TimeMillisData is zero`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)
        advanceTime(1000)

        val result = stopwatch.status

        val expect = StopwatchState.newStopped()
        assertEquals(expect, result.value)
    }


    @Test
    fun `When a stopwatch is running then its value is updated every 20 milliseconds`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)

        givenTimestamp(0)
        stopwatch.start()

        givenTimestamp(1)
        advanceTime(10)
        stopwatch.status.value shouldBe TimeMillisData("00:00:001", 1)
        stopwatch.status.value shouldBe StopwatchState.Running::class

        givenTimestamp(2)
        advanceTime(10)
        stopwatch.status.value shouldBe TimeMillisData("00:00:002", 2)
        stopwatch.status.value shouldBe StopwatchState.Running::class

        givenTimestamp(3)
        advanceTime(10)
        stopwatch.status.value shouldBe TimeMillisData("00:00:003", 3)
        stopwatch.status.value shouldBe StopwatchState.Running::class
    }

    @Test
    fun `When a stopwatch is paused then its value should not change`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)

        givenTimestamp(0)
        stopwatch.start()
        givenTimestamp(1)
        advanceTime(10)

        stopwatch.status.value shouldBe StopwatchState.Running::class

        stopwatch.pause()
        givenTimestamp(2)
        advanceTime(1000)

        stopwatch.status.value shouldBe StopwatchState.Paused::class
        stopwatch.status.value shouldBe TimeMillisData("00:00:001", 1)
    }

    @Test
    fun `When the stopwatch is resumed after being paused it continues running from the last value after 1000 seconds`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)

        stopwatch.start()
        var lastTime = advanceTimeItems(0,10, 100)
        stopwatch.status.value shouldBe StopwatchState.Running::class

        stopwatch.pause()
        lastTime = advanceTimeItems(lastTime, 10, 100)
        stopwatch.status.value shouldBe StopwatchState.Paused::class

        stopwatch.start()
        advanceTimeItems(lastTime, 10, 10)
        stopwatch.status.value shouldBe StopwatchState.Running::class

        stopwatch.stop()

        val result = stopwatch.status.value
        result shouldBe StopwatchState.Stopped::class
        result shouldBe TimeMillisData("00:01:100", 1100)
    }

    @Test
    fun `When a stopwatch is stopped it is saved status data`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)

        stopwatch.start()
        val lastTime = advanceTimeItems(0, 10, 100)
        stopwatch.status.value shouldBe StopwatchState.Running::class

        stopwatch.stop()
        stopwatch.status.value shouldBe StopwatchState.Stopped::class
        stopwatch.status.value shouldBe TimeMillisData("00:01:000", 1000)

        advanceTimeItems(lastTime, 10, 10)
        advanceTime(1000)

        val result = stopwatch.status.value
        stopwatch.status.value shouldBe StopwatchState.Stopped::class
        result shouldBe TimeMillisData("00:01:000", 1000)
    }

    private fun advanceTime(amount: Long) {
        coroutineDispatcher.scheduler.advanceTimeBy(amount)
    }

    private fun givenTimestamp(value: Long) {
        fakeTimestampProvider.mutableCurrentMillisecond = value
    }

    private infix fun StopwatchState.shouldBe(value: TimeMillisData) {
        assertEquals(value, this.timeMillisData)
    }
    private infix fun StopwatchState.shouldBe(value: KClass<out StopwatchState>) {
        assertEquals(value, this::class)
    }


    private fun advanceTimeItems(lastTime: Long, stepTime: Long, times: Int): Long {
        for (i in 1..times) {
            givenTimestamp(lastTime + (stepTime* i))
            advanceTime(stepTime)
        }
        return lastTime + (stepTime * times)
    }

}

