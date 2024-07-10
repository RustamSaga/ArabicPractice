import ArabicPractice.common.database.data.DesktopTestBuildConfig
import com.russaga.arabicpractice.AppDatabase
import com.russaga.arabicpractice.CityLocationQueries
import com.russaga.arabicpractice.CityNameQueries
import com.russaga.arabicpractice.GetCityLocationById
import com.russaga.arabicpractice.platform.DbDriverFactory
import com.russaga.arabicpractice.platform.PlatformConfiguration
import com.russaga.arabicpractice.platform.createDatabase
import kotlinx.coroutines.runBlocking
import model.City
import model.Location
import org.junit.Assert
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
class DesktopDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var cityLocationQueries: CityLocationQueries
    private lateinit var cityNameQueries: CityNameQueries

    @BeforeTest
    fun init() = runBlocking {

        val destinationDatabasePath = DesktopTestBuildConfig.testDatabasePath + DesktopTestBuildConfig.testDatabaseName

        val driver = DbDriverFactory(
            PlatformConfiguration(
            databasePath = destinationDatabasePath,
            isTest = true
        )
        )
        database = createDatabase(driver)
        cityLocationQueries = database.cityLocationQueries
        cityNameQueries = database.cityNameQueries
    }

    @Test
    fun insertCity() = runBlocking {
        cityLocationQueries.insertCityLocation(
            cityId = 343,
            latitude = 21.43f,
            longitude = 39.83f
        )
        cityNameQueries.insertCityName(343, 1, "مكة")
        cityNameQueries.insertCityName(343, 2, "Мекка")
        cityNameQueries.insertCityName(343, 2, "Mecca")

        val actualCity: GetCityLocationById = cityLocationQueries.getCityLocationById(343, 1).executeAsOne()
        val expected = GetCityLocationById(
            cityId = 343,
            name = "مكة",
            languageId = 1,
            latitude = 21.43f,
            longitude = 39.83f
        )
        assertEquals(expected, actualCity)
    }

    @Test
    fun gettingAllCities() = runBlocking {
        val actual = cityLocationQueries.getAllCities(1).executeAsList()
        Assert.assertEquals(343, actual.size)
    }

    @Test
    fun gettingCityLocation() = runBlocking {
        try {
            val result = cityLocationQueries.getCityLocationById(1, 2).executeAsOne()
            val actual = City(
                result.cityId,
                result.name,
                result.languageId,
                Location(result.latitude, result.longitude)
            )

            val expected = City(
                1, "Москва", 2, Location(55.75f, 37.62f)
            )

            Assert.assertEquals(actual, expected)
        }catch (e: NullPointerException) {
            fail(e.message)
        }
    }
}