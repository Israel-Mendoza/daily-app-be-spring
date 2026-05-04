package dev.artisra.dailyappkt.controllers

import dev.artisra.dailyappkt.models.requests.CreateTaskRequest
import dev.artisra.dailyappkt.models.responses.TaskDetailsResponse
import dev.artisra.dailyappkt.models.responses.TaskResponse
import dev.artisra.dailyappkt.services.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.findAll()
        if (tasks.isEmpty()) return ResponseEntity.noContent().build()
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Int): ResponseEntity<TaskResponse> {
        val task = taskService.findById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(task)
    }

    @GetMapping("/{id}/details")
    fun getTaskDetailsById(@PathVariable id: Int): ResponseEntity<TaskDetailsResponse> {
        val taskDetails = taskService.getTaskDetailsById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(taskDetails)
    }

    @PostMapping
    fun createTask(@RequestBody request: CreateTaskRequest): ResponseEntity<TaskResponse> {
        return ResponseEntity.ok(taskService.save(request))
    }

    @PatchMapping("/{id}")
    fun updateTask(@PathVariable id: Int, @RequestBody request: CreateTaskRequest): ResponseEntity<TaskResponse> {
        val updatedTask = taskService.update(id, request)
        return ResponseEntity.ok(updatedTask)
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Int): ResponseEntity<Unit> {
        taskService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/start")
    fun startTask(@PathVariable id: Int): ResponseEntity<Unit> {
        taskService.startTask(id)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/{id}/complete")
    fun completeTask(@PathVariable id: Int): ResponseEntity<Unit> {
        taskService.completeTask(id)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/{id}/reopen")
    fun reopenTask(@PathVariable id: Int): ResponseEntity<Unit> {
        taskService.reopenTask(id)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/{id}/cancel")
    fun cancelTask(@PathVariable id: Int): ResponseEntity<Unit> {
        if (id <= 0) return ResponseEntity.badRequest().build()
        taskService.cancelTask(id)
        return ResponseEntity.accepted().build()
    }
}