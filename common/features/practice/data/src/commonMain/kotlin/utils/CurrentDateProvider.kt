package utils

import kotlinx.datetime.LocalDateTime
import lessons_analize_info.todayDateTime

interface CurrentDateProvider{
    val today: LocalDateTime
        get() = todayDateTime
}