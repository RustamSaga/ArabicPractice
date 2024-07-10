package calendar.interfaces

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface CurrentGregorianDateProvider {
    val today: LocalDate
    val dateTime: LocalDateTime
}