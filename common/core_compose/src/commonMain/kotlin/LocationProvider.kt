import model.Location

interface LocationProvider {
    suspend fun getCurrentLocation(): Location?
}