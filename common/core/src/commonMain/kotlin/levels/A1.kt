package levels

import goals.Measurement

data class A1<M : Measurement>(override val name: String, override val standard: Standard.All<M>) :
    DifficultyLevel<M> {

}