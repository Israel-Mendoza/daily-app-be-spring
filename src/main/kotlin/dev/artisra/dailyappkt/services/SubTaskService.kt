package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.models.requests.CreateAndUpdateSubTaskRequest
import dev.artisra.dailyappkt.models.responses.SubTaskResponse
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SubTaskService(
    private val subTaskRepository: SubTaskRepository,
    private val taskRepository: TaskRepository,
    private val taskSynchronizerService: TaskSynchronizerService,
    private val subTaskPolicyService: SubTaskPolicyService,
) {

    fun findAll(): List<SubTaskResponse> = subTaskRepository.findAll().map { it.toSubTaskResponse() }

    fun findById(id: Int, taskId: Int): SubTaskResponse? {
        val subTask = subTaskRepository.findById(id).map { it.toSubTaskResponse() }.orElse(null)
        if (subTask == null) return null
        if (subTask.taskId != taskId) return null
        return subTask
    }

    fun findAllByTaskId(taskId: Int): List<SubTaskResponse> =
        subTaskRepository.findByTaskId(taskId).map { it.toSubTaskResponse() }

    fun save(taskId: Int, subTask: CreateAndUpdateSubTaskRequest): SubTaskResponse {
        val task = taskRepository.findById(taskId).orElse(null) ?: throw IllegalArgumentException("Task not found")
        val newSubTask = SubTask(task = task, title = subTask.title, isCompleted = false)
        taskSynchronizerService.syncTaskWithNewSubTasks(taskId)
        return subTaskRepository.save(newSubTask).toSubTaskResponse()
    }

    fun deleteById(taskId: Int, id: Int): Unit {
        val subTask = subTaskRepository.findById(id).orElse(null) ?: throw IllegalArgumentException("SubTask not found")
        if (subTask.task.id != taskId) throw IllegalArgumentException("SubTask does not belong to the specified task")
        subTaskRepository.deleteById(id)
    }

    fun update(taskId: Int, id: Int, subTask: CreateAndUpdateSubTaskRequest): SubTaskResponse {
        val existingSubTask = getSubTaskAndEnsureTaskOwnership(taskId, id)
        existingSubTask.title = subTask.title
        return subTaskRepository.save(existingSubTask).toSubTaskResponse()
    }

    fun complete(taskId: Int, id: Int): SubTaskResponse {
        val subTask = getSubTaskAndEnsureTaskOwnership(taskId, id)
        subTaskPolicyService.ensureCanCompleteSubTask(subTask)
        subTask.isCompleted = true
        return subTaskRepository.save(subTask).toSubTaskResponse()
    }

    fun reopen(taskId: Int, id: Int) {
        val subTask = getSubTaskAndEnsureTaskOwnership(taskId, id)
        subTask.isCompleted = false
        subTaskRepository.save(subTask)
        taskSynchronizerService.syncTaskWithNewSubTasks(taskId)
    }

    private fun getSubTaskAndEnsureTaskOwnership(taskId: Int, subTaskId: Int): SubTask {
        val subTask =
            subTaskRepository.findById(subTaskId).orElseThrow { IllegalArgumentException("SubTask not found") }
        if (subTask.task.id != taskId) throw IllegalArgumentException("SubTask does not belong to the specified task")
        return subTask
    }

    companion object {
        private val log = LoggerFactory.getLogger(SubTaskService::class.java)

        fun SubTask.toSubTaskResponse() =
            SubTaskResponse(
                id = this.id!!,
                taskId = this.task.id!!,
                title = this.title,
                isCompleted = this.isCompleted,
                createdAt = this.createdAt!!.toString(),
                updatedAt = this.updatedAt!!.toString(),
            )
    }
}
