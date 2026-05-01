package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import org.springframework.stereotype.Service

@Service
class TaskPolicyService(
    private val blockerRepository: BlockerRepository,
) {

    /**
     * Ensures that the task status is not BLOCKED if there are unresolved blockers.
     */
    fun ensureCanTransitionStatus(task: Task, targetStatus: TaskStatus) {
        val taskId = task.id ?: throw IllegalArgumentException("Task not found")
        val currentStatus = TaskStatus.valueOf(task.status)

        if (targetStatus == TaskStatus.CANCELED) return

        if (currentStatus == TaskStatus.BLOCKED && targetStatus != TaskStatus.BLOCKED) {
            val hasUnresolvedBlockers = blockerRepository.findByTaskId(taskId).any { !it.isResolved }
            if (hasUnresolvedBlockers) {
                throw IllegalArgumentException("Cannot change task status from BLOCKED while unresolved blockers exist")
            }
        }
    }
}