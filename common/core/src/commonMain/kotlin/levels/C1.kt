package levels

import goals.Measurement

data class C1<M : Measurement>(override val name: String, override val standard: Standard.All<M>) :
    DifficultyLevel<M>