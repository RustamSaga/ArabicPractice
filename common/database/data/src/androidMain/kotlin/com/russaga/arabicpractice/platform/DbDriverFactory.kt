package com.russaga.arabicpractice.platform

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russaga.arabicpractice.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DbDriverFactory actual constructor(
    private val platformConfiguration: PlatformConfiguration,
) {
    actual suspend fun provideDbDriver(): SqlDriver {

        val database = platformConfiguration.androidContext.getDatabasePath(
            platformConfiguration.databaseName
        )

        if (!database.exists()) {
            val inputStream = platformConfiguration.androidContext.assets.open(
                platformConfiguration.assetsDatabaseName
            )
            val outputStream = withContext(Dispatchers.IO) {
                FileOutputStream(database.absolutePath)
            }
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }

        return AndroidSqliteDriver(
            AppDatabase.Schema.synchronous(),
            platformConfiguration.androidContext,
            platformConfiguration.databaseName
        )
    }

}