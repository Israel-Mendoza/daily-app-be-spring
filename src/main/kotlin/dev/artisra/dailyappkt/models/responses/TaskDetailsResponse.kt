package dev.artisra.dailyappkt.models.responses

data class TaskDetailsResponse(
    val id: Int,
    val title: String,
    val status: String,
    val createAt: String,
    val updateAt: String,
    val notes: List<NoteResponse>,
    val subTasks: List<SubTaskResponse>,
    val blockers: List<BlockerResponse>,
)