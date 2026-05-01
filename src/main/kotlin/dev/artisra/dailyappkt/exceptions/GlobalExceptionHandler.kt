package dev.artisra.dailyappkt.exceptions

import dev.artisra.dailyappkt.models.responses.ConflictExceptionResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.http.HttpResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OpenBlockersException::class)
    fun handleIllegalStateException(ex: OpenBlockersException): ResponseEntity<ConflictExceptionResponse> {
        log.error("OpenBlockersException: {}", ex.message)
        val httpStatus = HttpStatus.CONFLICT
        val response = buildConflictExceptionResponse(ex.message ?: "Illegal state", httpStatus)
        return ResponseEntity.status(httpStatus).body(response)
    }

    @OptIn(ExperimentalTime::class)
    @ExceptionHandler(OpenSubTasksException::class)
    fun handleIllegalStateException(ex: OpenSubTasksException): ResponseEntity<ConflictExceptionResponse> {
        log.error("OpenSubTasksException: {}", ex.message)
        val httpStatus = HttpStatus.CONFLICT
        val response =buildConflictExceptionResponse(ex.message ?: "Illegal state", httpStatus)
        return ResponseEntity.status(httpStatus).body(response)
    }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

        @OptIn(ExperimentalTime::class)
        private fun buildConflictExceptionResponse(message: String, httpResponse: HttpStatus): ConflictExceptionResponse {
            return ConflictExceptionResponse(message, status = httpResponse.value(), timestamp = Clock.System.now().toString())
        }
    }
}