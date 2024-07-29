package model

import utils.START_FORMATTED_TIME

/**
 * @param formattedTime format mm:ss:sss. Sample 01:23:456 or -02:14:963
 * @param timeMillis milliseconds. Sample 01:23:456 -> 83_456 milliseconds
 */
data class TimeMillisData(
    val formattedTime: String,
    val timeMillis: Long
) {
    companion object {
        fun newInstance(): TimeMillisData = TimeMillisData(
            START_FORMATTED_TIME, 0
        )
    }
}