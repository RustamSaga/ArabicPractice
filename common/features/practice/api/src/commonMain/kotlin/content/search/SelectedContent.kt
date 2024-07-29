package content.search

/**
 * Enum representing the selection status of content.
 */
enum class SelectedContent {
    /**
     * Indicates that the content is selected.
     */
    SELECTED,

    /**
     * Indicates that the content is not selected.
     */
    UNSELECTED,

    /**
     * Indicates all content, regardless of selection status.
     */
    ALL;

    fun SelectedContent(id: Int): SelectedContent {
        return when(id) {
            0 -> SELECTED
            1 -> UNSELECTED
            2 -> ALL
            else -> throw IllegalArgumentException("Unknown SelectedContent id: $id")
        }
    }
}