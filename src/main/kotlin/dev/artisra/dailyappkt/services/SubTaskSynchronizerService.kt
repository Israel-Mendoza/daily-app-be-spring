package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import org.springframework.stereotype.Service

@Service
class SubTaskSynchronizerService(
    private val blockerRepository: BlockerRepository,
    private val subTaskRepository: SubTaskRepository,
) {

    fun syncSubTaskWithBlockers(taskId: Int, subTaskId: Int?) {
        if (subTaskId == null) return

        val subTask = subTaskRepository.findById(subTaskId).orElseThrow { IllegalArgumentException("SubTask not found") }
        if (subTask.task.id != taskId) {
            throw IllegalArgumentException("SubTask does not belong to the specified task")
        }

        val hasUnresolvedBlockers = blockerRepository.findByTaskIdAndSubTaskId(taskId, subTaskId).any { !it.isResolved }

        if (hasUnresolvedBlockers && subTask.isCompleted) {
            subTask.isCompleted = false
            subTaskRepository.save(subTask)
        }
    }
}
