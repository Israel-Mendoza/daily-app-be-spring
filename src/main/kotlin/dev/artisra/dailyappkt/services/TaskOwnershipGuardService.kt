package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskOwnershipGuardService(
    private val taskRepository: TaskRepository,
    private val subTaskRepository: SubTaskRepository,
) {

    fun ensureTaskExists(taskId: Int): Task =
        taskRepository.findById(taskId).orElse(null)
            ?: throw IllegalArgumentException("Task not found")

    fun ensureSubTaskBelongsToTask(taskId: Int, subTaskId: Int?): SubTask? =
        if (subTaskId != null) {
            val subTask = subTaskRepository.findById(subTaskId).orElse(null)
                ?: throw IllegalArgumentException("SubTask not found")
            if (subTask.task.id != taskId) {
                throw IllegalArgumentException("SubTask does not belong to the specified task")
            }
            subTask
        } else {
            null
        }
}