package time

class FakeTimestampProvider: TimestampProviderApi {
    var mutableCurrentMillisecond: Long = 0
    override val currentMilliseconds: Long get() = mutableCurrentMillisecond
}