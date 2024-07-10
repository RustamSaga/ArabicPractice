import model.Location

actual class PlatformGPSLocationProvider : LocationProvider {
    override suspend fun getCurrentLocation(): Location? {
        TODO("Not yet implemented")
    }
}