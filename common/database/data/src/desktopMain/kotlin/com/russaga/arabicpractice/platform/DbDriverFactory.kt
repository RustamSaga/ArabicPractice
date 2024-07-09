package com.russaga.arabicpractice.platform

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russaga.arabicpractice.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Paths

actual class DbDriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {

    actual suspend fun provideDbDriver(): SqlDriver = withContext(Dispatchers.IO) {

        val resourcePath = platformConfiguration.databasePath

        val databaseFile = Paths.get(resourcePath).toFile()

        if (!databaseFile.exists()) {
            Files.createFile(databaseFile.toPath())
        }

        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        try {
            AppDatabase.Schema.awaitCreate(driver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext driver
    }
}