package content.search

enum class Practice {
    LISTENING, SPEAKING, READING, WRITING, VOCABULARY, TEXT_OVERVIEW;

    fun Practice(id: Int): Practice {
        return when(id) {
            0 -> LISTENING
            1 -> SPEAKING
            2 -> READING
            3 -> WRITING
            4 -> VOCABULARY
            5 -> TEXT_OVERVIEW
            else -> throw IllegalArgumentException("Unknown Practice id: $id")
        }
    }
}