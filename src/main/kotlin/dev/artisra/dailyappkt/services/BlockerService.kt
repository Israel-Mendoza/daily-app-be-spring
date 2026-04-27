package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Blocker
import dev.artisra.dailyappkt.models.requests.CreateBlockerRequest
import dev.artisra.dailyappkt.models.requests.UpdateBlockerRequest
import dev.artisra.dailyappkt.models.responses.BlockerResponse
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.entities.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BlockerService(
    private val blockerRepository: BlockerRepository,
    private val taskOwnershipGuardService: TaskOwnershipGuardService,
    private val taskSynchronizerService: TaskSynchronizerService,
    private val subTaskSynchronizerService: SubTaskSynchronizerService,
) {

    fun findAll(): List<BlockerResponse> = blockerRepository.findAll().map { it.toBlockerResponse() }

    fun findById(id: Int): BlockerResponse? =
        blockerRepository.findById(id).map { it.toBlockerResponse() }.orElse(null)

    fun findByTaskId(taskId: Int): List<BlockerResponse> =
        blockerRepository.findByTaskId(taskId).map { it.toBlockerResponse() }

    fun save(taskId: Int, blocker: CreateBlockerRequest): BlockerResponse {
        val task = ensureTaskExists(taskId)
        val subTask = ensureSubTaskBelongsToTask(taskId, blocker.subTaskId)

        val newBlocker = Blocker(
            task = task,
            subTask = subTask,
            reason = blocker.reason,
            isResolved = false
        )
        val savedBlocker = blockerRepository.save(newBlocker)
        taskSynchronizerService.syncTaskWithBlockers(taskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, savedBlocker.subTask?.id)
        return savedBlocker.toBlockerResponse()
    }

    fun replace(id: Int, taskId: Int, blocker: CreateBlockerRequest): BlockerResponse {
        val existingBlocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (existingBlocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        val task = ensureTaskExists(taskId)
        val subTask = ensureSubTaskBelongsToTask(taskId, blocker.subTaskId)

        existingBlocker.task = task
        existingBlocker.subTask = subTask
        existingBlocker.reason = blocker.reason
        val savedBlocker = blockerRepository.save(existingBlocker)
        taskSynchronizerService.syncTaskWithBlockers(taskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, savedBlocker.subTask?.id)
        return savedBlocker.toBlockerResponse()
    }

    fun update(id: Int, taskId: Int, blocker: UpdateBlockerRequest): BlockerResponse {
        val existingBlocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (existingBlocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        blocker.reason?.let { existingBlocker.reason = it }
        blocker.isResolved?.let { existingBlocker.isResolved = it }

        val previousSubTaskId = existingBlocker.subTask?.id

        if (blocker.subTaskId != null) {
            existingBlocker.subTask = ensureSubTaskBelongsToTask(taskId, blocker.subTaskId)
        }

        val savedBlocker = blockerRepository.save(existingBlocker)
        taskSynchronizerService.syncTaskWithBlockers(taskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, previousSubTaskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, savedBlocker.subTask?.id)
        return savedBlocker.toBlockerResponse()
    }

    fun deleteById(id: Int) {
        val blocker = blockerRepository.findById(id).orElseThrow { IllegalArgumentException("Blocker not found") }
        val taskId = blocker.task.id ?: throw IllegalArgumentException("Task not found")
        val subTaskId = blocker.subTask?.id
        blockerRepository.deleteById(id)
        taskSynchronizerService.syncTaskWithBlockers(taskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, subTaskId)
    }

    fun resolve(id: Int, taskId: Int): BlockerResponse {
        val blocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (blocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        blocker.isResolved = true
        val savedBlocker = blockerRepository.save(blocker)
        taskSynchronizerService.syncTaskWithBlockers(taskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, savedBlocker.subTask?.id)
        return savedBlocker.toBlockerResponse()
    }

    fun reopen(id: Int, taskId: Int): BlockerResponse {
        val blocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (blocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        blocker.isResolved = false
        val savedBlocker = blockerRepository.save(blocker)
        taskSynchronizerService.syncTaskWithBlockers(taskId)
        subTaskSynchronizerService.syncSubTaskWithBlockers(taskId, savedBlocker.subTask?.id)
        return savedBlocker.toBlockerResponse()
    }

    private fun ensureTaskExists(taskId: Int): Task =
        taskOwnershipGuardService.ensureTaskExists(taskId)

    private fun ensureSubTaskBelongsToTask(taskId: Int, subTaskId: Int?): SubTask? =
        taskOwnershipGuardService.ensureSubTaskBelongsToTask(taskId, subTaskId)

    companion object {
        private val log = LoggerFactory.getLogger(BlockerService::class.java)

        fun Blocker.toBlockerResponse() =
            BlockerResponse(
                id = this.id!!,
                taskId = this.task.id!!,
                subTaskId = this.subTask?.id,
                reason = this.reason,
                isResolved = this.isResolved,
                createdAt = this.createdAt!!.toString(),
                updatedAt = this.updatedAt!!.toString(),
            )
    }
}
