package dev.artisra.dailyappkt.models.requests

data class CreateSubTaskRequest(
    val title: String,
    val isCompleted: Boolean,
)
