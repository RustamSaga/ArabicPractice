package levels

import goals.Measurement

data class B1<M : Measurement>(override val name: String, override val standard: Standard.All<M>) :
    DifficultyLevel<M>