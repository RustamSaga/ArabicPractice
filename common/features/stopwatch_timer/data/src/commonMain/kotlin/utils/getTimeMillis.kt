package utils

import kotlinx.datetime.Clock

internal val getTimeMillis: Long get() = Clock.System.now().toEpochMilliseconds()