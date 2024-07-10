import model.City
import model.Location

class LocationManagerImpl(
    private val gpsLocationProvider: LocationProvider,
    private val citiesQueries: CitiesLocationDatabase
): LocationManager {
    override suspend fun getCurrentLocation(): Location? {
        return gpsLocationProvider.getCurrentLocation()
    }

    override suspend fun getCities(languageId: Int): List<City> {
        return citiesQueries.getAllCities(languageId)
    }
}