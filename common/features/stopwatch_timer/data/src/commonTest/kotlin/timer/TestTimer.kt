package timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import model.TimeMillisData
import state.TimerState
import time.ElapsedTimeCalculator
import time.FakeTimestampProvider
import time.TimestampProvider
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TestTimer {

    private val coroutineDispatcher = StandardTestDispatcher()
    private val coroutineScope = CoroutineScope(SupervisorJob() + coroutineDispatcher)
    private val fakeTimestampProvider = FakeTimestampProvider()
    private val elapsedTimeCalculator = ElapsedTimeCalculator(fakeTimestampProvider)

    @Test
    fun `Initially the timer status is stopped and TimeMillisData is zero`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        advanceTime(1000)

        val result = timer.status

        val expect = TimerState.newStopped()
        assertEquals(expect, result.value)
    }

    @Test
    fun `Test error when limit not set before start`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)

        val error = assertFails {
            timer.start()
        }
        assertEquals(
            "The timer limit must be at least 1000 milliseconds (1 second).",
            error.message
        )

    }

    @Test
    fun `Test error when limit is less than one second before start`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)

        val error = assertFails {
            timer.setLimit(0, 0)
            timer.start()
        }
        assertEquals(
            "The timer limit must be at least 1000 milliseconds (1 second).",
            error.message
        )

    }


    @Test
    fun `When a timer is running then its value is updated every 20 milliseconds`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)
        givenTimestamp(0)
        timer.start()

        givenTimestamp(10)
        advanceTime(10)
        timer.status.value shouldBe TimeMillisData("00:59:990", 59990)
        timer.status.value shouldBe TimerState.Running::class

        givenTimestamp(20)
        advanceTime(10)
        timer.status.value shouldBe TimeMillisData("00:59:980", 59980)
        timer.status.value shouldBe TimerState.Running::class

        givenTimestamp(30)
        advanceTime(10)
        timer.status.value shouldBe TimeMillisData("00:59:970", 59970)
        timer.status.value shouldBe TimerState.Running::class
    }

    @Test
    fun `When a timer is paused then its value should not change`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)
        givenTimestamp(0)
        timer.start()
        givenTimestamp(10)
        advanceTime(10)

        timer.status.value shouldBe TimerState.Running::class

        timer.pause()
        givenTimestamp(1000)
        advanceTime(1000)

        timer.status.value shouldBe TimerState.Paused::class
        timer.status.value shouldBe TimeMillisData("00:59:990", 59990)
    }

    @Test
    fun `When the timer is resumed after being paused it continues running from the last value after 1000 seconds`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(0, 5)
        timer.start()
        var lastTime = advanceTimeItems(0, 10, 500)
        timer.status.value shouldBe TimerState.Running::class
        timer.status.value shouldBe TimeMillisData("00:00:000", 0)

        timer.pause()
        lastTime = advanceTimeItems(lastTime, 10, 100)
        timer.status.value shouldBe TimerState.Paused::class
        timer.status.value shouldBe TimeMillisData("00:00:000", 0)

        timer.start()
        advanceTimeItems(lastTime, 10, 100)
        timer.status.value shouldBe TimerState.Running::class

        timer.stop()

        val result = timer.status.value
        result shouldBe TimerState.Stopped::class
        result shouldBe TimeMillisData("-00:01:000", -1000)
    }

    @Test
    fun `When a timer is stopped it is saved status data`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(0, 5)
        timer.start()
        val lastTime = advanceTimeItems(0, 10, 500)
        timer.status.value shouldBe TimerState.Running::class

        timer.stop()
        timer.status.value shouldBe TimerState.Stopped::class
        timer.status.value shouldBe TimeMillisData("00:00:000", 0)

        advanceTimeItems(lastTime, 10, 10)
        advanceTime(1000)

        val result = timer.status.value
        timer.status.value shouldBe TimerState.Stopped::class
        result shouldBe TimeMillisData("00:00:000", 0)
    }

    @Test
    fun `Realtime test timer`() = runBlocking {
        var status: TimerState = TimerState.newStopped()
        val job = launch(Dispatchers.IO) {
            val timer = Timer(ElapsedTimeCalculator(TimestampProvider), this)
            timer.setLimit(0, 5)
            println("Status: ${status::class.simpleName}")
            timer.start()
            status = timer.status.value
            println("Status: ${status::class.simpleName}")
            launch(Dispatchers.IO) {
                timer.status.collect {
                    status = it
                    println(it.timeMillisData.formattedTime)
                }
            }
            delay(10000)
            timer.stop()
            status = timer.status.value
            println("Status: ${status::class.simpleName}")
            cancel()
        }

        job.join()

        println("Main coroutine finished")
        println("Finish Status: ${status::class.simpleName}")

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun advanceTime(amount: Long) {
        coroutineDispatcher.scheduler.advanceTimeBy(amount)
    }

    private fun givenTimestamp(value: Long) {
        fakeTimestampProvider.mutableCurrentMillisecond = value
    }

    private infix fun TimerState.shouldBe(value: TimeMillisData) {
        assertEquals(value, this.timeMillisData)
    }

    private infix fun TimerState.shouldBe(value: KClass<out TimerState>) {
        assertEquals(value, this::class)
    }


    private fun advanceTimeItems(lastTime: Long, stepTime: Long, times: Int): Long {
        for (i in 1..times) {
            givenTimestamp(lastTime + (stepTime * i))
            advanceTime(stepTime)
        }
        return lastTime + (stepTime * times)
    }

}