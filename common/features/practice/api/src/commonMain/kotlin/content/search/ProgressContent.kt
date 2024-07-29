package content.search

/**
 * Enum representing the progress status of content.
 */
enum class ProgressContent {
    /**
     * Indicates that the content is currently in progress.
     */
    IN_PROGRESS,
    /**
     * Indicates that the content is not in progress.
     */
    NOT_IN_PROGRESS,
    /**
     * Indicates all content, regardless of progress status.
     */
    ALL;

    fun ProgressContent(id: Int): ProgressContent {
        return when(id) {
            0 -> IN_PROGRESS
            1 -> NOT_IN_PROGRESS
            2 -> ALL
            else -> throw IllegalArgumentException("Unknown ProgressContent id: $id")
        }
    }
}