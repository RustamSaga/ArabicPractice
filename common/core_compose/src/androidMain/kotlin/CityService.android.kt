import model.Location
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL

actual class CityService {
    actual suspend fun getCityCoordinates(cityName: String): Location? {
        return try {
            val url = "http://api.geonames.org/searchJSON?q=$cityName&maxRows=1&username=demo"
            val json = URL(url).readText()
            val jsonObject = JSONObject(json)
            val latitude = jsonObject.getJSONArray("geonames").getJSONObject(0).getDouble("lat")
            val longitude = jsonObject.getJSONArray("geonames").getJSONObject(0).getDouble("lng")
            return Location(latitude.toFloat(), longitude.toFloat())
        } catch (e: MalformedURLException) {
            null
        }
    }
}