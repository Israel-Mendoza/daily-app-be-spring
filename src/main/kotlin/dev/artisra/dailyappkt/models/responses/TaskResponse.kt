package dev.artisra.dailyappkt.models.responses

data class TaskResponse(
    val id: Int,
    val title: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
)