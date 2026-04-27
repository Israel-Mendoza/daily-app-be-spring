package dev.artisra.dailyappkt.models.requests

data class UpdateNoteRequest(
    val subTaskId: Int?,
    val content: String?,
    val category: String?
)
