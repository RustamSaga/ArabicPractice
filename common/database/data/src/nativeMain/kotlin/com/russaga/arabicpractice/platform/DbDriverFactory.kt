package com.russaga.arabicpractice.platform

import com.russaga.arabicpractice.platform.PlatformConfiguration
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

actual class DbDriverFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual suspend fun provideDbDriver(): SqlDriver {
        TODO("Not yet implemented")
    }
}