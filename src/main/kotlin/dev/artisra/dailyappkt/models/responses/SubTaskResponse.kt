package dev.artisra.dailyappkt.models.responses

data class SubTaskResponse(
    val id: Int,
    val taskId: Int,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
)
