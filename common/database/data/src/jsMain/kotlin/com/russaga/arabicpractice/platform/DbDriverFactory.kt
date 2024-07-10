package com.russaga.arabicpractice.platform

import PlatformConfiguration
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

actual class DbDriverFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
        TODO("Not yet implemented")
    }
}