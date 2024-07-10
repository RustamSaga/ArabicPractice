package calendar

import calendar.date_generation.calculateHijriDate
import calendar.date_generation.isValidDateRange
import calendar.interfaces.CalendarManager
import calendar.interfaces.CurrentGregorianDateProvider
import calendar.model.CalendarDate
import calendar.model.HijriDayOfWeek
import calendar.model.HijriLocalDate
import calendar.model.HijriMonth
import calendar.model.HijriYear
import calendar.model.HijriYearInfo
import calendar.model.SearchableHijriDate
import exceptions.GregorianDateOutOfHijriRangeException
import exceptions.HijriDateNotFoundException
import exceptions.HijriYearNotFoundException
import exceptions.MonthDaysOutOfRangeException
import internal.getDaysFlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.serialization.json.Json
import utils.SharedFileReader
import utils.dateToday

internal const val DAYS_REMAINING_IN_YEAR = 47
internal const val DAYS_PASSED_IN_YEAR = 307
internal const val TOTAL_HIJRI_MONTHS = 12
internal const val DAYS_OF_WEEK = 7
internal const val NUMBER_OF_FIRST_MONTH = 1
internal const val NUMBER_OF_LAST_MONTH = 12
internal const val LAST_DAY_OF_DECEMBER = 31
internal const val FIRST_DAY = 1
internal val MAX_GREGORIAN_DATE_IN_HIJRI = LocalDate(2174, 11, 25)
internal val MIN_GREGORIAN_DATE_IN_HIJRI = LocalDate(2015, 10, 14)
internal val MIN_HIJRI_DATE = Triple(1447, 1, 1)
internal val MAX_HIJRI_DATE = Triple(1600, 12, 29)
internal val REFERENCE_DATES = Pair(
    first = LocalDate(year = 2024, monthNumber = 5, dayOfMonth = 20),
    second = HijriLocalDate(
        year = 1445,
        month = HijriMonth.ZU_AL_QAADAH,
        dayOfMonth = 12,
        dayOfWeek = HijriDayOfWeek.AL_ITHNAYN
    )
)

/**
 * first of all to call the initialize function
 */
class CalendarManagerImpl(
    private val currentGregorianDateProvider: CurrentGregorianDateProvider,
    sharedFileReader: SharedFileReader,
    fileName: String
) : CalendarManager {

    private val _hijriChronologies: MutableMap<Int, HijriYearInfo> = mutableMapOf()
    override val hijriChronologies: Map<Int, HijriYearInfo> = _hijriChronologies

    override val calendarsByHijriKey = mutableMapOf<Int, CalendarDate>()
    override val calendarByGregorianKey: MutableStateFlow<Map<Int, CalendarDate>> =
        MutableStateFlow(emptyMap())

    init {
        val jsonString = sharedFileReader.loadJsonFile(fileName)
        _hijriChronologies.putAll(Json.decodeFromString<List<HijriYear>>(jsonString)
            .asSequence() // Используем последовательность для ленивых вычислений
            .map { it.toHijriYearInfo(it.daysOfMonths.sum()) }
            .associateBy { it.year }
        )
    }

    override suspend fun getFirstThreeYearsFromPrevious() {
        val currentDate = dateToday
        if (currentDate > MAX_GREGORIAN_DATE_IN_HIJRI || currentDate < MIN_GREGORIAN_DATE_IN_HIJRI)
            throw GregorianDateOutOfHijriRangeException(currentDate)

        val firstDayOfCurrentYear = LocalDate(currentDate.year, Month.JANUARY, FIRST_DAY)

        val previousLocalDate = when {
            currentDate.year - 1 < MIN_GREGORIAN_DATE_IN_HIJRI.year -> null
            currentDate.year - 1 == MIN_GREGORIAN_DATE_IN_HIJRI.year -> MIN_GREGORIAN_DATE_IN_HIJRI
            else -> firstDayOfCurrentYear.minus(1, DateTimeUnit.YEAR)
        }
        val nextLocalDate = when {
            currentDate.year + 1 > MAX_GREGORIAN_DATE_IN_HIJRI.year -> null
            currentDate.year + 1 == MAX_GREGORIAN_DATE_IN_HIJRI.year -> MAX_GREGORIAN_DATE_IN_HIJRI
            else -> firstDayOfCurrentYear.plus(1, DateTimeUnit.YEAR)
        }

        coroutineScope {
            launch {
                val lastDate = LocalDate(currentDate.year, Month.DECEMBER, LAST_DAY_OF_DECEMBER)
                generateDates(firstDayOfCurrentYear, lastDate)
            }

            previousLocalDate?.let {
                val lastDate = LocalDate(it.year, Month.DECEMBER, LAST_DAY_OF_DECEMBER)
                launch {
                    generateDates(it, lastDate)
                }
            }
            nextLocalDate?.let {
                val lastDate = LocalDate(it.year, Month.DECEMBER, LAST_DAY_OF_DECEMBER)
                launch {
                    generateDates(it, lastDate)
                }
            }
        }
    }


    override val today: CalendarDate
        get() {
            val currentGregorianDate = currentGregorianDateProvider.today
            val key = currentGregorianDate.toEpochDays()
            return try {
                calendarByGregorianKey.value.getValue(key)
            } catch (e: NoSuchElementException) {
                val hijriLocalDate = hijriLocalDate(currentGregorianDate)
                val result = CalendarDate(currentGregorianDate, hijriLocalDate)
                calendarsByHijriKey[result.hijriLocalDate.key] = result
                calendarByGregorianKey.update { currentMap ->
                    currentMap + (key to result)
                }
                result
            }
        }

    override suspend fun getDate(localDate: LocalDate): CalendarDate {
        try {
            return calendarByGregorianKey.value.getValue(localDate.toEpochDays())
        } catch (e: NoSuchElementException) {
            try {
                val hijriLocalDate = hijriLocalDate(localDate)
                val calendarDate = CalendarDate(localDate, hijriLocalDate)
                val key = localDate.toEpochDays()
                calendarsByHijriKey[hijriLocalDate.key] = calendarDate
                calendarByGregorianKey.update { calendar ->
                    calendar + (key to calendarDate)
                }
                return calendarByGregorianKey.value.getValue(localDate.toEpochDays())
            } catch (e: Exception) {
                throw GregorianDateOutOfHijriRangeException(localDate)
            }
        }
    }

    override suspend fun getDate(hijriDate: SearchableHijriDate): CalendarDate {

        dateValidation(hijriDate.year, hijriDate.month, hijriDate.dayOfMonth)

        throw return try {
            calendarsByHijriKey.getValue(hijriDate.key)
        } catch (_: NoSuchElementException) {
            try {
                val result = calculateHijriDate(hijriDate)
                calendarsByHijriKey[result.hijriLocalDate.key] = result
                calendarByGregorianKey.update {
                    it + (result.gregorianLocalDate.toEpochDays() to result)
                }
                result
            } catch (e: HijriDateNotFoundException) {
                throw e
            }
        }
    }

    override fun getDatesRange(
        fromLocalDate: LocalDate,
        toLocalDate: LocalDate
    ): Flow<Pair<Int, CalendarDate>> = flow {
        val epochDaysRange =
            if (fromLocalDate < toLocalDate) fromLocalDate.toEpochDays()..toLocalDate.toEpochDays()
            else fromLocalDate.toEpochDays() downTo toLocalDate.toEpochDays()


        val existingDates = mutableListOf<Int>()
        for (key in epochDaysRange) {
            if (calendarByGregorianKey.value.containsKey(key)) {
                existingDates.add(key)
                emit(key to calendarByGregorianKey.value.getValue(key))
            }
        }

        val missingDateRange = epochDaysRange.filter { it !in existingDates }

        if (missingDateRange.isNotEmpty()) {
            generateDates(missingDateRange.first(), missingDateRange.last()) {
                calendarsByHijriKey[it.second.hijriLocalDate.key] = it.second
                emit(it)
            }
        }
    }

    /**
     * @throws HijriDateNotFoundException
     */
    override fun getDatesRange(
        fromHijriDate: SearchableHijriDate,
        toHijriDate: SearchableHijriDate
    ): Flow<Pair<Int, CalendarDate>> = flow {
        try {
            val fromLocalDate = calendarsByHijriKey.getValue(fromHijriDate.key).gregorianLocalDate
            val toLocalDate = calendarsByHijriKey.getValue(toHijriDate.key).gregorianLocalDate
            val epochDaysRange =
                if (fromLocalDate < toLocalDate) fromLocalDate.toEpochDays()..toLocalDate.toEpochDays()
                else fromLocalDate.toEpochDays() downTo toLocalDate.toEpochDays()


            val existingDates = mutableListOf<Int>()
            for (key in epochDaysRange) {
                if (calendarByGregorianKey.value.containsKey(key)) {
                    existingDates.add(key)
                    emit(key to calendarByGregorianKey.value.getValue(key))
                }
            }

            val missingDateRange = epochDaysRange.filter { it !in existingDates }

            if (missingDateRange.isNotEmpty()) {
                generateDates(missingDateRange.first(), missingDateRange.last()) {
                    calendarsByHijriKey[it.second.hijriLocalDate.key] = it.second
                    emit(it)
                }
            }
        } catch (_: NoSuchElementException) {
            try {
                val fromLocalDate = calculateHijriDate(fromHijriDate).gregorianLocalDate
                val toLocalDate = calculateHijriDate(toHijriDate).gregorianLocalDate

                val epochDaysRange =
                    if (fromLocalDate < toLocalDate) fromLocalDate.toEpochDays()..toLocalDate.toEpochDays()
                    else fromLocalDate.toEpochDays() downTo toLocalDate.toEpochDays()


                val existingDates = mutableListOf<Int>()
                for (key in epochDaysRange) {
                    if (calendarByGregorianKey.value.containsKey(key)) {
                        existingDates.add(key)
                        emit(key to calendarByGregorianKey.value.getValue(key))
                    }
                }

                val missingDateRange = epochDaysRange.filter { it !in existingDates }

                if (missingDateRange.isNotEmpty()) {
                    generateDates(missingDateRange.first(), missingDateRange.last()) {
                        calendarsByHijriKey[it.second.hijriLocalDate.key] = it.second
                        emit(it)
                    }
                }
            } catch (e: HijriDateNotFoundException) {
                throw e
            }
        }
    }



    fun hijriLocalDate(localDate: LocalDate): HijriLocalDate {
        if (localDate > MAX_GREGORIAN_DATE_IN_HIJRI || localDate < MIN_GREGORIAN_DATE_IN_HIJRI)
            throw GregorianDateOutOfHijriRangeException(localDate)

        return hijriChronologies.calculateHijriDate(localDate)
    }

    override suspend fun generateDates(startEpochDays: Int, endEpochDays: Int) {

        try {
            getDaysFlow(startEpochDays, endEpochDays).collect { (key, calendarDate) ->
                calendarByGregorianKey.update { calendar -> calendar + (key to calendarDate) }
            }
        } catch (_: GregorianDateOutOfHijriRangeException) {
        }
    }

    override suspend fun generateDates(
        startEpochDays: Int,
        nextDays: Int,
        result: suspend (Pair<Int, CalendarDate>) -> Unit
    ) {

        try {
            getDaysFlow(startEpochDays, nextDays).collect { (key, calendarDate) ->
                calendarByGregorianKey.update { calendar -> calendar + (key to calendarDate) }
                result(key to calendarDate)
            }
        } catch (_: GregorianDateOutOfHijriRangeException) {
        }
    }

    override suspend fun generateDates(
        startLocalDate: LocalDate,
        endLocalDate: LocalDate,
    ) {
        generateDates(startLocalDate.toEpochDays(), endLocalDate.toEpochDays())
    }

    private fun dateValidation(year: Int, month: HijriMonth, dayOfMonth: Int) {
        if (!isValidDateRange(year, month, dayOfMonth)) {
            throw HijriYearNotFoundException(year)
        }
        try {
            val range = 1..hijriChronologies.getValue(year).daysOfMonths[month.ordinal]
            if (dayOfMonth !in range)
                throw MonthDaysOutOfRangeException(range.last, dayOfMonth)
        } catch (e: NoSuchElementException) {
            throw HijriDateNotFoundException("If the date is within the calendar bounds, you might want to check your JSON file")
        }
    }
}