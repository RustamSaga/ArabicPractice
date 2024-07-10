package sunset_source

import com.batoulapps.adhan2.data.CalendarUtil.toUtcInstant
import com.batoulapps.adhan2.internal.toDegrees
import com.batoulapps.adhan2.internal.toRadians
import kotlinx.datetime.LocalTime
import model.Location
import sunset_source.interfaces.SunsetTimeProvider
import utils.timeNowUTC
import utils.todayUTC
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class SunsetTimeProviderImpl() : SunsetTimeProvider {

    override suspend fun isSunsetTime(location: Location?): Boolean {
        val currentSunset = currentSunset(location)
        return if (currentSunset != null) {
            timeNowUTC > currentSunset
        } else {
            false
        }
    }

    override suspend fun sunsetTime(location: Location): LocalTime =
        sunsetOf(location, todayUTC.toUtcInstant().epochSeconds)

    private var ha: Float = 0f // Угол часа
    private var noonT: Int = 0 // Время полудня в минутах

    fun computeSunset(lat: Float, lon: Float, unix: Long, gmt: Int = 0): Int {
        val latRad = lat.toDouble().toRadians()
//        val hours = (unix % 86400L) / 86400.0

        // Точные вычисления
        val julianCent = (unix / 86400.0 - 10957.5) / 36525.0
        val geomMeanLong = (280.46646 + julianCent * (36000.76983 + julianCent * 0.0003032)) % 360
        val geomMeanAnom = (357.52911 + julianCent * (35999.05029 - julianCent * 0.0001537))
        val eccEarth = 0.016708634 - julianCent * (0.000042037 + 0.0000001267 * julianCent)
        val sunEqCtr =
            sin(geomMeanAnom.toRadians()) * (1.914602 - julianCent * (0.004817 + 0.000014 * julianCent)) +
                    sin(2 * geomMeanAnom.toRadians()) * (0.019993 - 0.000101 * julianCent) +
                    sin(3 * geomMeanAnom.toRadians()) * 0.000289
        val sunApp =
            geomMeanLong + sunEqCtr - 0.0001 - 0.00008 * sin((125.04 - 1934.136 * julianCent).toRadians())
        val meanOblq =
            23 + (26 + ((21.448 - julianCent * (46.815 + julianCent * (0.00059 - julianCent * 0.001813)))) / 60.0) / 60.0
        val obliqCor = meanOblq + 0.00256 * cos((125.04 - 1934.136 * julianCent).toRadians())
        val decl = asin(sin(obliqCor.toRadians()) * sin(sunApp.toRadians())).toDegrees()
        val y = tan((obliqCor / 2).toRadians()).pow(2)
        val eqTime =
            4 * (y * sin(2 * geomMeanLong.toRadians()) - 2 * eccEarth * sin(geomMeanAnom.toRadians()) +
                    4 * eccEarth * y * sin(geomMeanAnom.toRadians()) * cos(2 * geomMeanLong.toRadians()) -
                    0.5 * y.pow(2) * sin(4 * geomMeanLong.toRadians()) - 1.25 * eccEarth.pow(2) * sin(
                2 * geomMeanAnom.toRadians()
            )).toDegrees().toFloat()

        ha =
            acos(-0.01454 / (cos(latRad) * cos(decl.toRadians())) - tan(latRad) * tan(decl.toRadians()))
                .toDegrees().toFloat()
        val gmtInMinutes = if (abs(gmt) <= 12) gmt * 60 else gmt
        noonT = (720 - 4 * lon - eqTime + gmtInMinutes).toInt()
        return noonT + (ha * 4).toInt()
    }

    suspend fun currentSunset(location: Location?): LocalTime? {
        return if (location != null)
            sunsetOf(location, todayUTC.toUtcInstant().epochSeconds)
        else null
    }

    fun sunsetOf(location: Location, epochDays: Long): LocalTime {
        val minutes = computeSunset(
            location.latitude,
            location.longitude,
            epochDays
        )
        val hour = minutes / 60
        val min = minutes % 60
        return LocalTime(hour, min)
    }

}

