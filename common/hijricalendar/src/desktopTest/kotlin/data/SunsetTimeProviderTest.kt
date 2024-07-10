package data

import com.batoulapps.adhan2.data.CalendarUtil.toUtcInstant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import sunset_source.SunsetTimeProviderImpl
import utils.todayDateTime
import utils.todayUTC
import kotlin.test.Test

class SunsetTimeProviderTest {

    @Test
    fun testSunset() {
        val sunPosition = SunsetTimeProviderImpl()
        val unix = LocalDateTime(
            2024,
            1,
            9,
            20,
            20
        ).toUtcInstant().epochSeconds// Clock.System.now().epochSeconds
//        41.071226658242765, 28.896089763307515
        val minutes = sunPosition.computeSunset(41.07f, 28.89f, unix) // Берлин, Unix Time для примера

        println(minutes)
        val hour = minutes / 60
        val min = minutes % 60
        println("sunset time - $hour:$min")

        val timeNowUTC = todayUTC
        val timeNowUTC2 = todayDateTime.toInstant(TimeZone.currentSystemDefault())
        val timeNow = todayDateTime

        println(timeNowUTC)
        println(timeNowUTC2)
        println(timeNow)

        // Проверьте значение заката
//        assertEquals(expectedSunsetTime, sunPosition.sunsetOf())
    }

}