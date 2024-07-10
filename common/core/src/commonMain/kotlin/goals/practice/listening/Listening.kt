package goals.practice.listening

import goals.Content
import goals.practice.Practice

data class Listening(
    override val title: String,
    override val id: Int,
    override val content: Content<ListeningResult>
) : Practice<ListeningResult> {
    val allWorkTime = content.repetitions.values.sumOf { it.result.time.toSeconds() }
    val amount = content.repetitions.keys.size
}