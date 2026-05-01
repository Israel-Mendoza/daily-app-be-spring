package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskSynchronizerService(
    private val blockerRepository: BlockerRepository,
    private val subTaskRepository: SubTaskRepository,
    private val taskRepository: TaskRepository,
) {

    fun syncTaskWithBlockers(taskID: Int) {
        val task = getTask(taskID)

        if (task.status == TaskStatus.CANCELED.toString()) return

        val blockers = blockerRepository.findByTaskId(taskID)

        task.status = if (blockers.any { !it.isResolved }) {
            TaskStatus.BLOCKED.toString()
        } else if (task.status == TaskStatus.DONE.toString()) {
            task.status
        } else {
            TaskStatus.IN_PROGRESS.toString()
        }

        taskRepository.save(task)
    }

    fun syncTaskWithSubtasks(taskId: Int) {
        val task = getTask(taskId)

        val subTasks = subTaskRepository.findByTaskId(taskId)

        task.status = if (subTasks.any { !it.isCompleted }) {
            TaskStatus.IN_PROGRESS.toString()
        } else {
            task.status
        }

        taskRepository.save(task)
    }

    private fun getTask(taskId: Int) = taskRepository.findById(taskId).orElseThrow { IllegalArgumentException("Task not found") }
}