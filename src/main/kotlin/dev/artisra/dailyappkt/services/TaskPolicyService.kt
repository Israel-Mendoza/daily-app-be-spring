package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.exceptions.OpenBlockersException
import dev.artisra.dailyappkt.exceptions.OpenSubTasksException
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import org.springframework.stereotype.Service

@Service
class TaskPolicyService(
    private val blockerRepository: BlockerRepository,
    private val subTaskRepository: SubTaskRepository,
) {

    /**
     * Ensures that the task status can be transitioned to the target status by checking for blockers and subtasks.
     */
    fun ensureCanTransitionStatus(task: Task, targetStatus: TaskStatus) {
        ensureCanUnblockTask(task, targetStatus)
        ensureCanCompleteTask(task, targetStatus)
    }

    /**
     * Ensures that the task status is not BLOCKED if there are unresolved blockers.
     */
    private fun ensureCanUnblockTask(task: Task, targetStatus: TaskStatus) {
        val currentStatus = TaskStatus.valueOf(task.status)

        if (targetStatus == TaskStatus.CANCELED) return

        if (currentStatus == TaskStatus.BLOCKED && targetStatus != TaskStatus.BLOCKED) {
            val hasUnresolvedBlockers = blockerRepository.findByTaskId(task.id!!).any { !it.isResolved }
            if (hasUnresolvedBlockers) {
                throw OpenBlockersException("Cannot change task status from BLOCKED while unresolved blockers exist")
            }
        }
    }

    /**
     * Ensures that the task can be completed if there are no incomplete subtasks.
     */
    private fun ensureCanCompleteTask(task: Task, targetStatus: TaskStatus) {
        val hasIncompleteSubtasks = subTaskRepository.findByTaskId(task.id!!).any { !it.isCompleted }
        if (targetStatus == TaskStatus.DONE && hasIncompleteSubtasks) {
            throw OpenSubTasksException("Cannot change task status to DONE while incomplete subtasks exist")
        }
    }
}