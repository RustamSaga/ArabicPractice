package practice.reading.options


interface MicrophonePermissionManager {
    suspend fun ifGranted(block: suspend () -> Unit)
    object Enable: MicrophonePermissionManager {
        override suspend fun ifGranted(block: suspend () -> Unit) = block()

    }
    object Disable: MicrophonePermissionManager {
        override suspend fun ifGranted(block: suspend () -> Unit) = Unit
    }
}