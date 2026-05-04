package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.models.requests.CreateTaskRequest
import dev.artisra.dailyappkt.models.responses.BlockerResponse
import dev.artisra.dailyappkt.models.responses.NoteResponse
import dev.artisra.dailyappkt.models.responses.SubTaskResponse
import dev.artisra.dailyappkt.models.responses.TaskDetailsResponse
import dev.artisra.dailyappkt.models.responses.TaskResponse
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val blockerService: BlockerService,
    private val subTaskService: SubTaskService,
    private val noteService: NoteService,
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
        val existingTask = getTaskFromRepository(id)
        existingTask.title = task.title
        return taskRepository.save(existingTask).toTaskResponse()
    }

    fun deleteById(id: Int) = taskRepository.deleteById(id)

    fun startTask(id: Int) {
        val existingTask = getTaskFromRepository(id)
        taskPolicyService.ensureCanTransitionStatus(existingTask, TaskStatus.IN_PROGRESS)
        log.info("Starting task: {}", existingTask.title)
        existingTask.status = TaskStatus.IN_PROGRESS.toString()
        taskRepository.save(existingTask)
    }

    fun completeTask(id: Int) {
        val existingTask = getTaskFromRepository(id)
        taskPolicyService.ensureCanTransitionStatus(existingTask, TaskStatus.DONE)
        existingTask.status = TaskStatus.DONE.toString()
        taskRepository.save(existingTask)
    }

    fun reopenTask(id: Int) {
        val existingTask = getTaskFromRepository(id)
        taskPolicyService.ensureCanTransitionStatus(existingTask, TaskStatus.TODO)
        existingTask.status = TaskStatus.TODO.toString()
        taskRepository.save(existingTask)
    }

    fun cancelTask(id: Int) {
        val existingTask = getTaskFromRepository(id)
        existingTask.status = TaskStatus.CANCELED.toString()
        taskRepository.save(existingTask)
    }

    fun getTaskDetailsById(taskId: Int): TaskDetailsResponse? {
        val task = getTaskFromRepository(taskId) ?: return null
        val subtasks: List<SubTaskResponse> = subTaskService.findAllByTaskId(taskId)
        val blockers: List<BlockerResponse> = blockerService.findByTaskId(taskId)
        val notes: List<NoteResponse> = noteService.findByTaskId(taskId)
        return TaskDetailsResponse(
            id = task.id!!,
            title = task.title,
            status = task.status,
            createAt = task.createdAt?.toString() ?: "",
            updateAt = task.updatedAt?.toString() ?: "",
            notes = notes,
            subTasks = subtasks,
            blockers = blockers
        )
    }

    private fun getTaskFromRepository(taskId: Int) =
        taskRepository.findById(taskId).orElseThrow { IllegalArgumentException("Task not found") }

    companion object {

        private val log = LoggerFactory.getLogger(TaskService::class.java)

        fun Task.toTaskResponse() = TaskResponse(
            id = this.id!!,
            title = this.title,
            status = this.status,
            createdAt = this.createdAt?.toString() ?: "",
            updatedAt = this.updatedAt?.toString() ?: ""
        )
    }
}
