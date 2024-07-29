package content.search

/**
 * Enum representing the completion status of content.
 */
enum class CompletedContent {
    /**
     * Indicates that the content is completed.
     */
    COMPLETED,

    /**
     * Indicates that the content is not completed.
     */
    UNCOMPLETED,

    /**
     * Indicates all content, regardless of completion status.
     */
    ALL;

    fun CompletedContent(id: Int): CompletedContent {
        return when(id) {
            0 -> COMPLETED
            1 -> UNCOMPLETED
            2 -> ALL
            else -> throw IllegalArgumentException("Unknown CompletedContent id: $id")
        }
    }
}