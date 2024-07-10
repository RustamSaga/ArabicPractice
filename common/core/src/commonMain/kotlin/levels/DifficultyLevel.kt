package levels

import goals.Measurement

interface DifficultyLevel<M: Measurement> {
    val name: String
    val standard: Standard.All<M>
}