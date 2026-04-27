package dev.artisra.dailyappkt.models.requests

import dev.artisra.dailyappkt.models.enums.NoteCategory

data class CreateNoteRequest(
    val subTaskId: Int?,
    val content: String,
    val category: String = NoteCategory.GENERAL.name,
)