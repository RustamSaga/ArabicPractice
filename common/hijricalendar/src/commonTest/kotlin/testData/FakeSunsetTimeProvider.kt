package testData

import calendar.model.CalendarDate
import kotlinx.datetime.LocalTime
import model.Location
import sunset_source.interfaces.SunsetTimeProvider

class FakeSunsetTimeProvider(
    var timeNow: LocalTime,
    private val sunsetTime: LocalTime = LocalTime(19, 0)
) : SunsetTimeProvider {
    override suspend fun isSunsetTime(location: Location?): Boolean {
        return if (location != null)
            timeNow >= sunsetTime(location)
        else false
    }

    override suspend fun sunsetTime(location: Location): LocalTime {
        return sunsetTime
    }
}