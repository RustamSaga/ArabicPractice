package goals.practice

import goals.AtTatbiq
import goals.Content
import levels.Standard
import goals.Measurement

interface Practice<M: Measurement>: AtTatbiq {
    val id: Int
    val title: String
    val content: Content<M>
}