package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskSynchronizerService(
    private val blockerRepository: BlockerRepository,
    private val taskRepository: TaskRepository,
) {

    fun syncTaskWithBlockers(taskID: Int) {
        val task = taskRepository.findById(taskID).orElseThrow { IllegalArgumentException("Task not found") }

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
}