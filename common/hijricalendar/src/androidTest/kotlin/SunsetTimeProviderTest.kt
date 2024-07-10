import sunset_source.SunsetTimeProviderImpl
import com.batoulapps.adhan2.data.CalendarUtil.toUtcInstant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test

class SunsetTimeProviderTest {

    @Test
    fun testSunset() {
        val sunPosition = SunsetTimeProviderImpl()
        val timeUTC = LocalDateTime(
            2024,
            1,
            9,
            20,
            20
        ).toUtcInstant().epochSeconds// Clock.System.now().epochSeconds
//        41.071226658242765, 28.896089763307515
        val minutes = sunPosition.computeSunset(41.07f, 28.89f, timeUTC) // Берлин, Unix Time для примера

        println(minutes)
        val hour = minutes / 60
        val min = minutes % 60
        println("sunset time - $hour:$min")
        TimeZone.availableZoneIds.forEach {
            println(it)
        }
        // Проверьте значение заката
//        assertEquals(expectedSunsetTime, sunPosition.sunset())
    }

}