package practice.reading
/**
 * Data class representing the status of a word in a text.
 *
 * This class is used to keep track of each word in a text and whether it has been read or not.
 * The text is divided into individual words, and a list of `WordStatus` objects is created to store
 * the status of each word. This allows for easy tracking and updating of the reading status of
 * each word in the text.
 *
 *
 * @param isRead A boolean indicating whether the word has been read.
 * @param word The word in the text.
 */
data class WordStatus(
    val isRead: Boolean,
    val word: String
)
