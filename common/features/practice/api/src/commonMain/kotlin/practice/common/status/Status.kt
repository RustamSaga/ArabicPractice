package practice.common.status

import practice.common.result.MainResult

/**
 * @param id A unique identifier for the record
 * @param contentId A unique identifier for the content (text) associated with this record.
 * @param maxRepeatNumber The maximum number of repetitions required to complete the reading of this content.
 * @param currentRepeatNumber The current number of repetitions completed for this content.
 * @param lastResult The result of the last reading session.
 * @param bestResult The best reading result achieved by the user.
 * @param isSelected A flag indicating whether this content is selected for the current reading session.
 * @param isCompleted A flag indicating whether the reading of this content is completed. Returns true if the current number of repetitions (currentRepeatNumber) is greater than or equal to the maximum number of repetitions (maxRepeatNumber).
 *
 */
interface Status<T: MainResult> {
    val id: Int
    val contentId: Int
    val maxRepeatNumber: Int
    val currentRepeatNumber: Int
    val lastResult: T
    val bestResult: T
    val isSelected: Boolean
    val isCompleted: Boolean
        get() = currentRepeatNumber >= maxRepeatNumber

}