package goals

import androidx.compose.ui.text.AnnotatedString
import levels.DifficultyLevel

data class Content<M: Measurement>(
    val id: Int,
    val text: AnnotatedString, // TODO может мне нужно создать свой класс
    val difficultyLevel: DifficultyLevel<M>,
    val numberOfWords: Int,
    val repetitions: Map<NumberReps, PracticeRes<M>>,
    val standard: PracticeRes<M>
) {

}

