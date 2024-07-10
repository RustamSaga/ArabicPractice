import model.Location

expect class CityService {
    suspend fun getCityCoordinates(cityName: String): Location?
}

