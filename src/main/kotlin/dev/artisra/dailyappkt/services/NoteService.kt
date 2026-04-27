package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Note
import dev.artisra.dailyappkt.models.requests.CreateNoteRequest
import dev.artisra.dailyappkt.models.requests.UpdateNoteRequest
import dev.artisra.dailyappkt.models.responses.NoteResponse
import dev.artisra.dailyappkt.repositories.NoteRepository
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val taskOwnershipGuardService: TaskOwnershipGuardService,
) {

    fun findByTaskId(taskId: Int): List<NoteResponse> =
        noteRepository.findByTaskId(taskId).map { it.toNoteResponse() }

    fun findById(id: Int, taskId: Int): NoteResponse? {
        val note = noteRepository.findById(id).orElse(null) ?: return null
        if (note.task.id != taskId) return null
        return note.toNoteResponse()
    }

    fun save(taskId: Int, note: CreateNoteRequest): NoteResponse {
        val task = taskOwnershipGuardService.ensureTaskExists(taskId)

        val subTask = validateAndGetSubTask(taskId, note.subTaskId)

        val newNote = Note(
            task = task,
            subTask = subTask,
            content = note.content,
            category = note.category,
        )
        return noteRepository.save(newNote).toNoteResponse()
    }

    fun replace(id: Int, taskId: Int, note: CreateNoteRequest): NoteResponse {
        val existingNote = noteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Note not found") }

        validateNoteOwnership(existingNote, taskId)

        val task = taskOwnershipGuardService.ensureTaskExists(taskId)

        val subTask = validateAndGetSubTask(taskId, note.subTaskId)

        existingNote.task = task
        existingNote.subTask = subTask
        existingNote.content = note.content
        existingNote.category = note.category
        return noteRepository.save(existingNote).toNoteResponse()
    }

    fun update(id: Int, taskId: Int, note: UpdateNoteRequest): NoteResponse {
        val existingNote = noteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Note not found") }

        validateNoteOwnership(existingNote, taskId)

        note.content?.let { existingNote.content = it }
        note.category?.let { existingNote.category = it }

        if (note.subTaskId != null) {
            existingNote.subTask = validateAndGetSubTask(taskId, note.subTaskId)
        }

        return noteRepository.save(existingNote).toNoteResponse()
    }

    fun deleteById(taskId: Int, id: Int) {
        val note = noteRepository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Note not found")
        validateNoteOwnership(note, taskId)
        noteRepository.deleteById(id)
    }

    private fun validateNoteOwnership(note: Note, taskId: Int) {
        if (note.task.id != taskId) {
            throw IllegalArgumentException("Note does not belong to the specified task")
        }
    }

    private fun validateAndGetSubTask(taskId: Int, subTaskId: Int?) =
        taskOwnershipGuardService.ensureSubTaskBelongsToTask(taskId, subTaskId)

    companion object {
        fun Note.toNoteResponse() =
            NoteResponse(
                id = this.id!!,
                taskId = this.task.id!!,
                subTaskId = this.subTask?.id,
                content = this.content,
                category = this.category,
                createdAt = this.createdAt!!.toString(),
                updatedAt = this.updatedAt!!.toString(),
            )
    }
}
