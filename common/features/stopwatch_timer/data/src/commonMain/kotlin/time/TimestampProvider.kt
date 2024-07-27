package time

import kotlinx.datetime.Clock

object TimestampProvider: TimestampProviderApi {
    override val currentMilliseconds: Long get() = Clock.System.now().toEpochMilliseconds()
}