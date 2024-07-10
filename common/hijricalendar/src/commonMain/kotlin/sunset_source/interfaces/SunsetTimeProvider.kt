package sunset_source.interfaces

import kotlinx.datetime.LocalTime
import model.Location

interface SunsetTimeProvider {
    suspend fun isSunsetTime(location: Location?): Boolean
    suspend fun sunsetTime(location: Location): LocalTime
}