package com.russaga.arabicpractice.di

import com.russaga.arabicpractice.platform.DbDriverFactory
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val databaseModule = DI.Module("databaseModule") {
    bind<DbDriverFactory>() with singleton {
        DbDriverFactory(instance())
    }
}