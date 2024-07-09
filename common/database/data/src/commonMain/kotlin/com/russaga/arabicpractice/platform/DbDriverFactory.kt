package com.russaga.arabicpractice.platform

import com.russaga.arabicpractice.platform.PlatformConfiguration
import app.cash.sqldelight.db.SqlDriver
import com.russaga.arabicpractice.AppDatabase
import com.russaga.arabicpractice.adapters.FloatColumnAdapter
import com.russaga.arabicpractice.adapters.LongColumnAdapter
import com.russaga.arabicpractice.adapters.cityLocationAdapter
import com.russaga.arabicpractice.adapters.cityNameAdapter
import migrations.CityLocation
import migrations.CityName

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DbDriverFactory(platformConfiguration: PlatformConfiguration) {
    suspend fun provideDbDriver(): SqlDriver
}

suspend fun createDatabase(driverFactory: DbDriverFactory): AppDatabase {
    val driver = driverFactory.provideDbDriver()
    return AppDatabase(
        driver = driver,
        cityLocationAdapter = cityLocationAdapter,
        cityNameAdapter = cityNameAdapter,
    )
}

