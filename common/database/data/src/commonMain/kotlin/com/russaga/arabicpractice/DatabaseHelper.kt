package com.russaga.arabicpractice

import com.russaga.arabicpractice.platform.DbDriverFactory
import com.russaga.arabicpractice.platform.createDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DatabaseHelper(private val driverFactory: DbDriverFactory) {

    private var db: AppDatabase? = null

    private val mutex = Mutex()

    suspend fun <Result: Any> withDatabase(block: suspend (AppDatabase) -> Result) : Result = mutex.withLock {
        if (db == null) {
            db = createDb(driverFactory)
        }
        return@withLock block(db!!)
    }

    private suspend fun createDb(driverFactory: DbDriverFactory) : AppDatabase {
        return createDatabase(driverFactory)
    }

}