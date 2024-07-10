import model.Location

actual class PlatformGPSLocationProvider : LocationProvider {
    override val locationFromZone: LocationFromZoneDatabase
        get() = TODO("Not yet implemented")

    override suspend fun getCurrentLocation(): Location? {
        TODO("Not yet implemented")
    }
}