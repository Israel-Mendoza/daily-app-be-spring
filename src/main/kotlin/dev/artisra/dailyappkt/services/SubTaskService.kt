package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.models.requests.CreateSubTaskRequest
import dev.artisra.dailyappkt.models.requests.UpdateSubTaskRequest
import dev.artisra.dailyappkt.models.responses.SubTaskResponse
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SubTaskService(
    private val subTaskRepository: SubTaskRepository,
    private val taskRepository: TaskRepository,
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

    fun save(taskId: Int, subTask: CreateSubTaskRequest): SubTaskResponse {
        val task = taskRepository.findById(taskId).orElse(null) ?: throw IllegalArgumentException("Task not found")
        subTaskPolicyService.ensureCanCreateOrAssignToTask(task.status)

        val newSubTask = SubTask(task = task, title = subTask.title, isCompleted = subTask.isCompleted)
        return subTaskRepository.save(newSubTask).toSubTaskResponse()
    }

    fun deleteById(taskId: Int, id: Int): Unit {
        val subTask = subTaskRepository.findById(id).orElse(null) ?: throw IllegalArgumentException("SubTask not found")
        if (subTask.task.id != taskId) throw IllegalArgumentException("SubTask does not belong to the specified task")
        subTaskRepository.deleteById(id)
    }

    fun update(taskId: Int, id: Int, subTask: UpdateSubTaskRequest): SubTaskResponse {
        val existingSubTask = getSubTaskAndEnsureTaskOwnership(taskId, id)
        subTaskPolicyService.ensureCanCreateOrAssignToTask(existingSubTask.task.status)

        existingSubTask.title = subTask.title ?: existingSubTask.title

        val targetIsCompleted = subTask.isCompleted ?: existingSubTask.isCompleted
        if (targetIsCompleted) {
            subTaskPolicyService.ensureCanCompleteSubTask(existingSubTask)
        }
        existingSubTask.isCompleted = targetIsCompleted

        return subTaskRepository.save(existingSubTask).toSubTaskResponse()
    }

    fun replace(taskId: Int, id: Int, subTask: CreateSubTaskRequest): SubTaskResponse? {
        val existingSubTask = getSubTaskAndEnsureTaskOwnership(taskId, id)
        subTaskPolicyService.ensureCanCreateOrAssignToTask(existingSubTask.task.status)

        existingSubTask.title = subTask.title
        if (subTask.isCompleted) {
            subTaskPolicyService.ensureCanCompleteSubTask(existingSubTask)
        }
        existingSubTask.isCompleted = subTask.isCompleted

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
