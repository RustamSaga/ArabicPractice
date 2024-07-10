package levels

import goals.PracticeRes
import goals.Measurement

interface Standard<M: Measurement> {

    val value: PracticeRes<M>

    class Beginner<M: Measurement>(override val value: PracticeRes<M>): Standard<M>
    class Middle<M: Measurement>(override val value: PracticeRes<M>): Standard<M>
    class Native<M: Measurement>(override val value: PracticeRes<M>): Standard<M>

    class All<M: Measurement>(
        val beginner: Beginner<M>,
        val middle: Middle<M>,
        val native: Native<M>
    )
}

