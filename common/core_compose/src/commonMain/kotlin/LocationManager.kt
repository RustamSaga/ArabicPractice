
import model.City
import model.Location

interface LocationManager{
    suspend fun getCurrentLocation(): Location?
    suspend fun getCities(languageId: Int): List<City>
}

