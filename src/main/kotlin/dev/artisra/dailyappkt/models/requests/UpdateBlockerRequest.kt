package dev.artisra.dailyappkt.models.requests

data class UpdateBlockerRequest(
    val subTaskId: Int?,
    val reason: String?,
    val isResolved: Boolean?,
)