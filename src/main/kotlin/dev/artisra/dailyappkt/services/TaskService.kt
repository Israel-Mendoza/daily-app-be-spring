package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.models.requests.CreateTaskRequest
import dev.artisra.dailyappkt.models.responses.TaskResponse
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskPolicyService: TaskPolicyService,
) {

    fun findAll(): List<TaskResponse> = taskRepository.findAll().map { it.toTaskResponse() }

    fun findById(id: Int): TaskResponse? = taskRepository.findById(id).map { it.toTaskResponse() }.orElse(null)

    fun findTaskById(id: Int): Task? = taskRepository.findById(id).orElse(null)

    fun save(task: CreateTaskRequest): TaskResponse {
        val newTask = Task(title = task.title)
        return taskRepository.save(newTask).toTaskResponse()
    }

    fun update(id: Int, task: CreateTaskRequest): TaskResponse {
        val existingTask = taskRepository.findById(id).orElseThrow { IllegalArgumentException("Task not found") }
        existingTask.title = task.title
        return taskRepository.save(existingTask).toTaskResponse()
    }

    fun deleteById(id: Int) = taskRepository.deleteById(id)

    fun startTask(id: Int) {
        val existingTask = taskRepository.findById(id).orElseThrow { IllegalArgumentException("Task not found") }
        taskPolicyService.ensureCanTransitionStatus(existingTask, TaskStatus.IN_PROGRESS)
        log.info("Starting task: {}", existingTask.title)
        existingTask.status = TaskStatus.IN_PROGRESS.toString()
        taskRepository.save(existingTask)
    }

    fun completeTask(id: Int) {
        val existingTask = taskRepository.findById(id).orElseThrow { IllegalArgumentException("Task not found") }
        taskPolicyService.ensureCanTransitionStatus(existingTask, TaskStatus.DONE)
        existingTask.status = TaskStatus.DONE.toString()
        taskRepository.save(existingTask)
    }

    fun reopenTask(id: Int) {
        val existingTask = taskRepository.findById(id).orElseThrow { IllegalArgumentException("Task not found") }
        taskPolicyService.ensureCanTransitionStatus(existingTask, TaskStatus.TODO)
        existingTask.status = TaskStatus.TODO.toString()
        taskRepository.save(existingTask)
    }

    fun cancelTask(id: Int) {
        val existingTask = taskRepository.findById(id).orElseThrow { IllegalArgumentException("Task not found") }
        existingTask.status = TaskStatus.CANCELED.toString()
        taskRepository.save(existingTask)
    }

    companion object {

        private val log = LoggerFactory.getLogger(TaskService::class.java)

        fun Task.toTaskResponse() = TaskResponse(
            id = this.id!!,
            title = this.title,
            status = this.status,
            createAt = this.createdAt?.toString() ?: "",
            updateAt = this.updatedAt?.toString() ?: ""
        )
    }
}
