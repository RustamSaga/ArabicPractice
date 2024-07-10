package com.russaga.arabicpractice.platform

actual class PlatformConfiguration(
    private val isTest: Boolean,
//    val databaseImportFileName: String,
    val databasePath: String
) {

}