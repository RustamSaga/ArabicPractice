package internal

internal const val HIJRI_YEAR_START = 1437
internal const val HIJRI_YEAR_END = 1600
internal fun isValidYear(year: Int): Boolean =
            year in HIJRI_YEAR_START..HIJRI_YEAR_END

