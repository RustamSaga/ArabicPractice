package goals.practice.listening

import utils.WorkTime
import goals.Measurement
//import utils.IDate

data class ListeningResult(
    override val id: Int,
    val time: WorkTime,
//    val date: IDate
): Measurement
