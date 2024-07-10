package exceptions

import kotlinx.datetime.LocalDate

class HijriDateNotFoundException(message: String) :
    Exception("Date out of range. Min = 1437-1-1 for 2015-10-14, max = 1600-12-30 for 2174-11-25. Note: $message")

class GregorianDateOutOfHijriRangeException(localDate: LocalDate) :
    Exception("The $localDate date is out of range. Min = 2015-10-14 for 1447-1-1, max = 2174-11-25 for 1600-12-30.")

class HijriYearNotFoundException(year: Int) :
    Exception("Invalid date: the year $year is out of range. Min = 1437, max = 1600")

class HijriMonthException(month: Int) :
    Exception("Invalid date: month must be a number between 1 and 12, got $month")

class MonthDaysOutOfRangeException(maxDayInMonth: Int, targetDay: Int) :
    Exception("Invalid date: day of month must be a number between 1 and $maxDayInMonth, got $targetDay")

class HijriWeekNotFoundException() :
    NoSuchElementException("Element not found, you must initialize your week dates.")

class CursorOutOfBoundsException(cursor: Int, note: String) :
    NoSuchElementException("Cursor out of bounds: $cursor. Note: $note")