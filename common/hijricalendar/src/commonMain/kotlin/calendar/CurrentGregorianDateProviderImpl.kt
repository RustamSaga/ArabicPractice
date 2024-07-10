package calendar

import calendar.interfaces.CurrentGregorianDateProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import utils.dateToday
import utils.todayDateTime

class CurrentGregorianDateProviderImpl: CurrentGregorianDateProvider {
    override val today: LocalDate
        get() = dateToday
    override val dateTime: LocalDateTime
        get() = todayDateTime

}
