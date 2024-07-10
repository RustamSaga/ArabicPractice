package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val todayDateTime: LocalDateTime
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

val dateToday: LocalDate = todayDateTime.date

val timeNow: LocalTime = todayDateTime.time

val todayUTC: LocalDateTime
    get() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

val dateTodayUTC: LocalDate
    get() = todayUTC.date

val timeNowUTC: LocalTime
    get() = todayUTC.time
