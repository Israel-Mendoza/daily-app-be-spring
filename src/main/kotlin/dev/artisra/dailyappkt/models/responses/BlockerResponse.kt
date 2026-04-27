package dev.artisra.dailyappkt.models.responses

data class BlockerResponse(
    val id: Int,
    val taskId: Int,
    val subTaskId: Int?,
    val reason: String,
    val isResolved: Boolean,
    val createdAt: String,
    val updatedAt: String,
)

