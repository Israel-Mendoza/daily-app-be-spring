package dev.artisra.dailyappkt.models.requests

data class CreateBlockerRequest(
    val subTaskId: Int?,
    val reason: String,
)
