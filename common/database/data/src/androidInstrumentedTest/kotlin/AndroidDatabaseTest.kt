import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

private const val TEST_DB_NAME = "TestArabicPracticeDatabase.db"
private const val DB_NAME = "AppDatabase.db"

class AndroidDatabaseTest {

    private lateinit var cityLocationQueries: CityLocationQueries
    private lateinit var cityNameQueries: CityNameQueries
    private lateinit var db: AppDatabase

    @Before
    fun init() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val driver = DbDriverFactory(
            PlatformConfiguration(
                context,
                databaseName = DB_NAME,
                assetsDatabaseName = TEST_DB_NAME
            )
        )
        db = createDatabase(driver)
        cityLocationQueries = db.cityLocationQueries
        cityNameQueries = db.cityNameQueries
    }

    @After
    fun tearDown() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val originalDbPath = context.getDatabasePath(DB_NAME).absolutePath
        deleteDatabase(originalDbPath)

    }

    @Test
    fun insertWasSucceed() = runBlocking {

        val expected1 = GetCityLocationById(400, "فلان", 1, 1000f, 1000f)
        val expected2 = GetCityLocationById(400, "Фулан", 2, 1000f, 1000f)
        val expected3 = GetCityLocationById(400, "Fulan", 3, 1000f, 1000f)

        val actualNullFulan1 = cityLocationQueries.getCityLocationById(400, 1)
        val actualNullFulan2 = cityLocationQueries.getCityLocationById(400, 2)
        val actualNullFulan3 = cityLocationQueries.getCityLocationById(400, 3)

        assertEquals(null, actualNullFulan1.executeAsOneOrNull())
        assertEquals(null, actualNullFulan2.executeAsOneOrNull())
        assertEquals(null, actualNullFulan3.executeAsOneOrNull())


        cityLocationQueries.insertCityLocation(400, 1000f, 1000f)
        cityNameQueries.insertCityName(400, 1, "فلان")
        cityNameQueries.insertCityName(400, 2, "Фулан")
        cityNameQueries.insertCityName(400, 3, "Fulan")


        val actualFulan1 = cityLocationQueries.getCityLocationById(400, 1)
        val actualFulan2 = cityLocationQueries.getCityLocationById(400, 2)
        val actualFulan3 = cityLocationQueries.getCityLocationById(400, 3)
        assertEquals(expected1, actualFulan1.executeAsOne())
        assertEquals(expected2, actualFulan2.executeAsOne())
        assertEquals(expected3, actualFulan3.executeAsOne())

    }

    @Test
    fun gettingAllCities() = runBlocking {
        val actual = cityLocationQueries.getAllCities(1).executeAsList()
        assertEquals(342, actual.size)
    }

    @Test
    fun gettingCityLocation() = runBlocking {

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

        assertEquals(actual, expected)
    }

    private fun deleteDatabase(dbPath: String) {
        val dbFile = File(dbPath)
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

}

