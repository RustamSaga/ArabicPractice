*Kotlin Multiplatform Date Converter Library*

**Init main class**

```kotlin

    val calendarManager = CalendarManagerImpl(
        currentGregorianDateProvider = CurrentGregorianDateProviderImpl(),
        sharedFileReader = SharedFileReader(platformParams), // platform class
        fileName = "hijrichronology.json" // you can change file
    )
    
    // initialization converter class
    val syncCalendar = SyncCalendarProvider(
        readCalendarManager = calendarManager,
        sunsetTimeProvider = SunsetTimeProviderImpl()
    )

```

**Example of single date**

```kotlin

val gregorianDate = LocalDate(2024, 2, 1)
val syncDate: CalendarDate = syncCalendar.getDate(localDate = gregorianDate)

val hijriDate: SearchableHijriDate = SearchableHijriDate(1444, HijriMonth.RAMADAN, 1)
val syncDate: CalendarDate = syncCalendar.getDate(hijriDate = hijriDate)

val syncCurrentDate: CalendarDate = syncCalendar.today()
// If you want to account for sunset, then you need to provide coordinates (Location) 
val location = Location(latitude = 0f, longitude = 0f)
val syncCurrentDate: CalendarDate = syncCalendar.today(location)

```

**Example of range of dates**

```kotlin
val fromLocalDate = LocalDate(2024, 2, 1)
val toLocalDate = LocalDate(2024,5,5) 

val dateRange: Flow<Pair<Int, CalendarDate>> = syncCalendar.getDateRange(fromLocalDate, toLocalDate)

// from hijri dates
val fromHijriDate = SearchableHijriDate(1444, HijriMonth.RAMADAN, 1)
val toHijriDate = SearchableHijriDate(1444, HijriMonth.ZU_AL_HIJJAH, 1)

val dateRange: Flow<Pair<Int, CalendarDate>> = syncCalendar.getDateRange(fromHijriDate, toHijriDate)
```

**Note**
```kotlin
    // SyncCalendarProvider has a mode - CalendarMode: Hijri or Gregorian. If we use Gregorian,
    // the 'today' function doesn't require a location because it's only needed for Hijri.
    
    val updatedMode = CalendarMode.HIJRI
    syncCalendar.updateCalendarMode(updatedMode)
    // Now, calculate sunset is enable, and you can use location cor calculate sunset
    val today = syncCalendar.today(Location(21.42f, 39.82f))
    

```
