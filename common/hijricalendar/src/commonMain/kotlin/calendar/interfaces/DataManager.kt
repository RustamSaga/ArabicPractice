package calendar.interfaces

import calendar.model.CalendarDate
import calendar.model.HijriYearInfo
import kotlinx.coroutines.flow.MutableStateFlow

interface DataManager {

    val today: CalendarDate

    val hijriChronologies: Map<Int, HijriYearInfo>

    val calendarsByHijriKey: MutableMap<Int, CalendarDate>

    val calendarByGregorianKey: MutableStateFlow<Map<Int, CalendarDate>>
}