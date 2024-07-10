import model.City

interface CitiesLocationDatabase {

    suspend fun getCityLocationById(cityId: Int, languageId: Int): City

    suspend fun getAllCities(languageId: Int): List<City>

    suspend fun insertCityLocation(city: City)
}