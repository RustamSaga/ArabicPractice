package content.search

enum class ContentLevel {
    A0, A1, A2, B1, B2, C1, C2;

    fun ContentLevel(id: Int): ContentLevel {
        return when(id) {
            0 -> A0
            1 -> A1
            2 -> A2
            3 -> B1
            4 -> B2
            5 -> C1
            6 -> C2
            else -> throw IllegalArgumentException("Unknown ContentLevel id: $id")
        }
    }
}