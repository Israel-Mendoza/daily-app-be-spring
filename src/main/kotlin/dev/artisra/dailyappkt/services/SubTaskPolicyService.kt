package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import org.springframework.stereotype.Service

@Service
class SubTaskPolicyService(
    private val blockerRepository: BlockerRepository,
) {

    /**
     * Ensures that the task status is not DONE or CANCELED.
     */
    fun ensureCanCreateOrAssignToTask(taskStatus: String) {
        if (taskStatus == TaskStatus.DONE.toString() || taskStatus == TaskStatus.CANCELED.toString()) {
            throw IllegalArgumentException("Cannot create or assign subtasks to a task that is DONE or CANCELED")
        }
    }

    /**
     * Ensures that the subTask can be completed by checking if it has any unresolved blockers.
     */
    fun ensureCanCompleteSubTask(subTask: SubTask) {
        val subTaskId = subTask.id ?: throw IllegalArgumentException("SubTask not found")
        val taskId = subTask.task.id ?: throw IllegalArgumentException("Task not found")

        val hasUnresolvedBlockers = blockerRepository.findByTaskIdAndSubTaskId(taskId, subTaskId).any { !it.isResolved }
        if (hasUnresolvedBlockers) {
            throw IllegalArgumentException("Cannot complete subtask with unresolved blockers")
        }
    }
}