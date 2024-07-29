package utils

import kotlin.math.ceil

// TODO список должен состоять только из слов. Нужно переписать сплит
fun String.toListWords(): List<String> = split(" ")
fun String.numberOfWords(): Int = toListWords().size
fun Int.getSeconds(speed: Int): Int {
    val minutes = this / speed.toFloat()
    val seconds = minutes * 60
    return ceil(seconds).toInt()
}

