package dev.artisra.dailyappkt.models.responses

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class ConflictExceptionResponse(
    val message: String,
    val status: Int,
    val timestamp: String = Clock.System.now().toString()
) {
    override fun toString(): String {
        return "ConflictExceptionResponse(message='$message', status=$status, timestamp='$timestamp')"
    }
}
