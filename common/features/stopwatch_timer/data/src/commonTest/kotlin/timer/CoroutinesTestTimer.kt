package timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import time.ElapsedTimeCalculator
import time.FakeTimestampProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class CoroutinesTestTimer {

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
    fun `When the first timer is started then scope should become active`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)
        timer.start()
        advanceTime(1000)
        coroutineScope shouldHaveChildrenCount 1
    }
    @Test
    fun `When the first timer is started multiple times then scope should become active`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)

        timer.start()
        timer.start()
        timer.start()
        advanceTime(1000)
        coroutineScope shouldHaveChildrenCount 1
    }

    @Test
    fun `When the last task is stopped then the scope should become inactive`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)

        timer.start()

        advanceTime(1000)

        timer.stop()

        coroutineScope shouldHaveChildrenCount 0
    }

    @Test
    fun `When a stopped timer is started again then the scope should become active`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)

        timer.start()

        advanceTime(1000)
        timer.stop()

        timer.start()

        coroutineScope shouldHaveChildrenCount 1
    }

    @Test
    fun `When timer is paused then the scope should become inactive`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)

        timer.start()
        advanceTime(1000)

        timer.pause()

        coroutineScope shouldHaveChildrenCount 0
    }

    @Test
    fun `When a paused timer is started then the scope should become active`() {
        val timer = Timer(elapsedTimeCalculator, coroutineScope)
        timer.setLimit(1, 0)

        timer.start()
        advanceTime(1000)

        timer.pause()
        timer.start()

        coroutineScope shouldHaveChildrenCount 1
    }


    private infix fun CoroutineScope.shouldHaveChildrenCount(count: Int) {
        advanceTime(1)
        val job = coroutineScope.coroutineContext[Job]!!
        assertEquals(count, job.children.count())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun advanceTime(amount: Int) {
        coroutineDispatcher.scheduler.advanceTimeBy(amount.toLong())
    }
}