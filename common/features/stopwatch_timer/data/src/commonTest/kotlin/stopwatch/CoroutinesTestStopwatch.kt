package stopwatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import time.ElapsedTimeCalculator
import time.FakeTimestampProvider
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTestStopwatch {

    private val coroutineDispatcher = StandardTestDispatcher()
    private val coroutineScope = CoroutineScope(SupervisorJob() + coroutineDispatcher)
    private val fakeTimestampProvider = FakeTimestampProvider()
    private val elapsedTimeCalculator = ElapsedTimeCalculator(fakeTimestampProvider)

    @Test
    fun `Initially the scope is inactive`() {
        advanceTime(1000)

        coroutineScope shouldHaveChildrenCount 0
    }

    @Test
    fun `When the first stopwatch is started then scope should become active`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)
        stopwatch.start()
        advanceTime(1000)
        coroutineScope shouldHaveChildrenCount 1
    }

    @Test
    fun `When the last task is stopped then the scope should become inactive`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)
        stopwatch.start()

        advanceTime(1000)

        stopwatch.stop()

        coroutineScope shouldHaveChildrenCount 0
    }

    @Test
    fun `When a stopped stopwatch is started again then the scope should become active`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)
        stopwatch.start()

        advanceTime(1000)
        stopwatch.stop()

        stopwatch.start()

        coroutineScope shouldHaveChildrenCount 1
    }

    @Test
    fun `When stopwatch is paused then the scope should become inactive`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)
        stopwatch.start()
        advanceTime(1000)

        stopwatch.pause()

        coroutineScope shouldHaveChildrenCount 0
    }

    @Test
    fun `When a paused stopwatch is started then the scope should become active`() {
        val stopwatch = Stopwatch(elapsedTimeCalculator, coroutineScope)
        stopwatch.start()
        advanceTime(1000)

        stopwatch.pause()
        stopwatch.start()

        coroutineScope shouldHaveChildrenCount 1
    }


    private infix fun CoroutineScope.shouldHaveChildrenCount(count: Int) {
        advanceTime(1)
        val job = coroutineScope.coroutineContext[Job]!!
        assertEquals(count, job.children.count())
    }

    private fun advanceTime(amount: Int) {
        coroutineDispatcher.scheduler.advanceTimeBy(amount.toLong())
    }
}

