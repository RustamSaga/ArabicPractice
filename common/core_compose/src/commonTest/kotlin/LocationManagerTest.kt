import kotlinx.coroutines.test.runTest
import model.City
import model.Location
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationManagerTest {
    data class CityDataBase(
        val englishName: String,
        val russianName: String,
        val arabicName: String,
        val location: Location
    )


    private val gpsLocationProvider = object : LocationProvider {
        override suspend fun getCurrentLocation(): Location? = Location(21.4225f, 39.8262f)
    }
    private val citiesLocationDatabase = object : CitiesLocationDatabase {

        private val cities = listOf(
            CityDataBase("New York", "Нью-Йорк", "نيويورك", Location(40.7128f, -74.0060f)),
            CityDataBase("London", "Лондон", "لندن", Location(51.5074f, -0.1278f)),
            CityDataBase("Tokyo", "Токио", "طوكيو", Location(35.6895f, 139.6917f)),
            CityDataBase("Paris", "Париж", "باريس", Location(48.8566f, 2.3522f)),
            CityDataBase("Moscow", "Москва", "موسكو", Location(55.7558f, 37.6173f)),
            CityDataBase("Cairo", "Каир", "القاهرة", Location(30.0444f, 31.2357f)),
            CityDataBase("Beijing", "Пекин", "بكين", Location(39.9042f, 116.4074f)),
            CityDataBase("Dubai", "Дубай", "دبي", Location(25.276987f, 55.296249f)),
            CityDataBase("Sydney", "Сидней", "سيدني", Location(-33.8688f, 151.2093f)),
            CityDataBase("Mecca", "Мекка", "مكة", Location(21.4225f, 39.8262f))
        )

        private fun CityDataBase.getName(languageId: Int) = when (languageId) {
            0 -> this.arabicName
            1 -> this.russianName
            2 -> this.englishName
            else -> throw IllegalArgumentException("id not found")
        }


        override suspend fun getCityLocationById(cityId: Int, languageId: Int): City {
            val result = City(
                id = cityId,
                name = cities[cityId].getName(languageId),
                location = cities[cityId].location,
                languageId = languageId
            )
            return result
        }

        override suspend fun getAllCities(languageId: Int): List<City> = cities.mapIndexed { index, cityDataBase ->
            City(
                id = index,
                name = cityDataBase.getName(languageId),
                location = cityDataBase.location,
                languageId = languageId
            )
        }

        override suspend fun insertCityLocation(city: City) {
            TODO("Not yet implemented")
        }

    }


    private val locationManager = LocationManagerImpl(
        gpsLocationProvider = gpsLocationProvider,
        citiesQueries = citiesLocationDatabase
    )

    @Test
    fun gettingCurrentLocationWasSuccessful() = runTest {
        val actual = locationManager.getCurrentLocation()
        val expected = Location(21.4225f, 39.8262f)

        assertEquals(expected, actual)
    }

    @Test
    fun gettingAllCitiesWasSuccessfull() = runTest {
        val actual = locationManager.getCities(0)
        val expected = listOf(
            City(id = 0, name ="نيويورك", location = Location(40.7128f, -74.0060f), languageId = 0),
            City(id = 1, name ="لندن", location = Location(51.5074f, -0.1278f), languageId = 0),
            City(id = 2, name ="طوكيو", location = Location(35.6895f, 139.6917f), languageId = 0),
            City(id = 3, name ="باريس", location = Location(48.8566f, 2.3522f), languageId = 0),
            City(id = 4, name ="موسكو", location = Location(55.7558f, 37.6173f), languageId = 0),
            City(id = 5, name ="القاهرة", location = Location(30.0444f, 31.2357f), languageId = 0),
            City(id = 6, name ="بكين", location = Location(39.9042f, 116.4074f), languageId = 0),
            City(id = 7, name ="دبي", location = Location(25.276987f, 55.296249f), languageId = 0),
            City(id = 8, name ="سيدني", location = Location(-33.8688f, 151.2093f), languageId = 0),
            City(id = 9, name ="مكة", location = Location(21.4225f, 39.8262f), languageId = 0)
        )

        assertEquals(expected, actual)
    }

}