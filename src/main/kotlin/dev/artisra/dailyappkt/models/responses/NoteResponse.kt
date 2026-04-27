package dev.artisra.dailyappkt.models.responses

data class NoteResponse(
    val id: Int,
    val taskId: Int,
    val subTaskId: Int?,
    val content: String,
    val category: String,
    val createdAt: String,
    val updatedAt: String,
)

