package com.russaga.arabicpractice

import ArabicPracticeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import model.City
import model.Location

class ArabicPracticeDatabaseImpl(
    private val dbHelper: DatabaseHelper,
    private val scope: CoroutineScope
): ArabicPracticeDatabase {

    override suspend fun getCityLocationById(cityId: Int, languageId: Int): City {
        val result = scope.async {
            dbHelper.withDatabase { database ->
                database.cityLocationQueries.getCityLocationById(
                    cityId, languageId
                ).executeAsOne().mapQuery()
            }
        }
        return result.await()
    }

    override suspend fun getAllCities(languageId: Int): List<City> {
        val result = scope.async {
            dbHelper.withDatabase { database ->
                database.cityLocationQueries.getAllCities(languageId).executeAsList().map {
                    it.mapQuery()
                }
            }
        }
        return result.await()
    }

    override suspend fun insertCityLocation(city: City) {
        scope.launch {
            dbHelper.withDatabase { database ->

            }
        }
    }

}

internal fun GetCityLocationById.mapQuery(): City = mapToCity(
    cityId, name, languageId, latitude, longitude
)

internal  fun GetAllCities.mapQuery(): City = mapToCity(
    cityId, name, languageId, latitude, longitude
)

internal fun mapToCity(
    cityId: Int,
    name: String,
    languageId: Int,
    latitude: Float,
    longitude: Float
): City = City(
    id = cityId,
    name = name,
    languageId = languageId,
    location = Location(latitude, longitude)
)