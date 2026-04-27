package dev.artisra.dailyappkt.controllers

import dev.artisra.dailyappkt.models.requests.CreateSubTaskRequest
import dev.artisra.dailyappkt.models.requests.UpdateSubTaskRequest
import dev.artisra.dailyappkt.models.responses.SubTaskResponse
import dev.artisra.dailyappkt.services.SubTaskService
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
import java.net.URI

@RestController
@RequestMapping("/tasks/{taskId}/subtasks")
class SubTaskController(
    private val subTaskService: SubTaskService
) {

    @GetMapping
    fun getSubTasksByTaskId(@PathVariable taskId: Int): ResponseEntity<List<SubTaskResponse>> {
        log.info("Getting all subtasks for task $taskId")
        val subTasks = subTaskService.findAllByTaskId(taskId)
        if (subTasks.isEmpty()) return ResponseEntity.noContent().build()
        return ResponseEntity.ok(subTasks)
    }

    @GetMapping("/{id}")
    fun getSubTaskById(@PathVariable taskId: Int, @PathVariable id: Int): ResponseEntity<SubTaskResponse> {
        val subTask = subTaskService.findById(id, taskId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(subTask)
    }

    @PostMapping
    fun createSubTask(@PathVariable taskId: Int, @RequestBody subTaskRequest: CreateSubTaskRequest): ResponseEntity<SubTaskResponse> {
        val createdSubTask = subTaskService.save(taskId, subTaskRequest)
        return ResponseEntity.created(URI.create("/tasks/$taskId/subtasks/${createdSubTask.id}")).body(createdSubTask)
    }

    @PutMapping("/{id}")
    fun replaceSubTask(@PathVariable taskId: Int, @PathVariable id: Int, @RequestBody subTaskRequest: CreateSubTaskRequest): ResponseEntity<SubTaskResponse> {
        val replacedSubTask = subTaskService.replace(taskId, id, subTaskRequest)
        return ResponseEntity.ok(replacedSubTask)
    }

    @PatchMapping("/{id}")
    fun updateSubTask(@PathVariable taskId: Int, @PathVariable id: Int, @RequestBody subTaskRequest: UpdateSubTaskRequest): ResponseEntity<SubTaskResponse> {
        val updatedSubTask = subTaskService.update(taskId, id, subTaskRequest)
        return ResponseEntity.ok(updatedSubTask)
    }

    @DeleteMapping("/{id}")
    fun deleteSubTask(@PathVariable taskId: Int, @PathVariable id: Int): ResponseEntity<Unit> {
        subTaskService.deleteById(taskId, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/complete")
    fun completeSubTask(@PathVariable taskId: Int, @PathVariable id: Int): ResponseEntity<Unit> {
        subTaskService.complete(taskId, id)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/{id}/reopen")
    fun reopenSubTask(@PathVariable taskId: Int, @PathVariable id: Int): ResponseEntity<Unit> {
        subTaskService.reopen(taskId, id)
        return ResponseEntity.accepted().build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(SubTaskController::class.java)
    }
}