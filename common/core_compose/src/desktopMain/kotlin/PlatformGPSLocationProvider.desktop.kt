import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import model.Location
import java.net.HttpURLConnection
import java.net.URL

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformGPSLocationProvider(
    private val lastSavedLocation: Location?
): LocationProvider {
    override suspend fun getCurrentLocation(): Location? = withContext(Dispatchers.IO) {
        try {
            val connection = URL("https://ipinfo.io/json").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.inputStream.use { inputStream ->
                val response = inputStream.bufferedReader().readText()
                val ipInfoResponse: IpInfoResponse = Json.decodeFromString(response)
                val loc = ipInfoResponse.loc.split(",")
                Location(loc[0].toFloat(), loc[1].toFloat())
            }
        } catch (e: Exception) {
//            e.printStackTrace()
            lastSavedLocation
        }
    }

}