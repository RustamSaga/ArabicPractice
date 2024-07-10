package com.russaga.arabicpractice.platform

import android.content.Context

actual class PlatformConfiguration (
    val androidContext: Context,
    val databaseName: String,
    val assetsDatabaseName: String
)