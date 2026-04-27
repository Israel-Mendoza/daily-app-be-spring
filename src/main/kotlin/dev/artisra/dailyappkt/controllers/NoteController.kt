package dev.artisra.dailyappkt.controllers

import dev.artisra.dailyappkt.models.requests.CreateNoteRequest
import dev.artisra.dailyappkt.models.requests.UpdateNoteRequest
import dev.artisra.dailyappkt.models.responses.NoteResponse
import dev.artisra.dailyappkt.services.NoteService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks/{taskId}/notes")
class NoteController(
    private val noteService: NoteService,
) {

    @GetMapping
    fun getNotesForTask(@PathVariable taskId: Int): ResponseEntity<List<NoteResponse>> {
        log.info("Getting all notes for task $taskId")
        val notes = noteService.findByTaskId(taskId)
        return if (notes.isEmpty()) ResponseEntity.noContent().build() else ResponseEntity.ok(notes)
    }

    @GetMapping("/{noteId}")
    fun getNote(@PathVariable taskId: Int, @PathVariable noteId: Int): ResponseEntity<NoteResponse> {
        log.info("Getting note $noteId for task $taskId")
        val note = noteService.findById(noteId, taskId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(note)
    }

    @PostMapping
    fun createNote(@PathVariable taskId: Int, @RequestBody request: CreateNoteRequest): ResponseEntity<NoteResponse> {
        log.info("Creating note for task $taskId")
        return ResponseEntity.ok(noteService.save(taskId, request))
    }

    @PutMapping("/{noteId}")
    fun replaceNote(
        @PathVariable taskId: Int,
        @PathVariable noteId: Int,
        @RequestBody request: CreateNoteRequest,
    ): ResponseEntity<NoteResponse> {
        log.info("Replacing note $noteId for task $taskId")
        return ResponseEntity.ok(noteService.replace(noteId, taskId, request))
    }

    @PatchMapping("/{noteId}")
    fun updateNote(
        @PathVariable taskId: Int,
        @PathVariable noteId: Int,
        @RequestBody request: UpdateNoteRequest,
    ): ResponseEntity<NoteResponse> {
        log.info("Updating note $noteId for task $taskId")
        return ResponseEntity.ok(noteService.update(noteId, taskId, request))
    }

    @DeleteMapping("/{noteId}")
    fun deleteNote(@PathVariable taskId: Int, @PathVariable noteId: Int): ResponseEntity<Unit> {
        log.info("Deleting note $noteId for task $taskId")
        noteService.deleteById(taskId, noteId)
        return ResponseEntity.noContent().build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(NoteController::class.java)
    }
}
