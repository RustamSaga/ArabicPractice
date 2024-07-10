package utils

data class WorkTime(
    val time: String
) {
    private val format: String = "hh:mm:ss"

    fun toSeconds(): Int {
        TODO()
    }
}
