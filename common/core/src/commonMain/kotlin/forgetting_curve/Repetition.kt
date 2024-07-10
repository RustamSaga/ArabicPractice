package forgetting_curve

/**
 * * На выбор предлагается 3 варианта запоминания, основанные на "кривой забывания Эббингауза"
 * * Для каждого следующего повторения программа уведомляет о необходимости повторить материал / практику
 * * Фича - если уведомления были пропущены (например 3 раза) - происходит откат прогресса на одну-две ступени ниже.
 *          возможно оставить выбор за пользователем (в начале приложения): проигнорировать уведомления без отката, либо осуществить откат
 *
 * * только для теории и практики по этой теории
 */

interface Repetition {
    val numberOfSkippedRepetitions: Int

    fun setNextRepetitionTime()
    fun getNextRepetitionTime(): RepetitionTime


    /**
     * - 1 - after 0
     * - 2 - after 10-20 min
     * - 3 - after 8-12 h
     * - 4 - after 24-32 h
     * - 5 - after 3-5 days
     */
    class Low : Repetition {
        private var _numberOfSkippedRepetitions: Int = 0
        override val numberOfSkippedRepetitions: Int
            get() = _numberOfSkippedRepetitions

        override fun setNextRepetitionTime() {
            TODO("Not yet implemented")
        }

        override fun getNextRepetitionTime(): RepetitionTime {
            TODO("Not yet implemented")
        }
    }

    /**
     * - 1 - after 0
     * - 2 - after 20-30 min
     * - 3 - after 24-32 h
     * - 4 - after 2-3 week
     * - 5 - after 2-3 month
     */
    class Middle : Repetition {
    }

    /**
     * * 1 - after 0
     * * 2 - after 25 sec
     * * 3 - after 2 min
     * * 4 - after 10 min
     * * 5 - after 1 H
     * * 6 - after 5 H
     * * 7 - after 1 day
     * * 8 - after 5 days
     * * 9 - after 25 days
     * * 10 - after 4 months
     * * 11 - after 2 years
     */
    class High : Repetition {

    }
}