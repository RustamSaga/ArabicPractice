package com.russaga.arabicpractice.adapters

import app.cash.sqldelight.ColumnAdapter

internal val LongColumnAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}
internal val FloatColumnAdapter = object : ColumnAdapter<Float, Double> {
    override fun decode(databaseValue: Double): Float = databaseValue.toFloat()
    override fun encode(value: Float): Double = value.toDouble()
}