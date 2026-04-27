package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Blocker
import dev.artisra.dailyappkt.models.requests.CreateBlockerRequest
import dev.artisra.dailyappkt.models.requests.UpdateBlockerRequest
import dev.artisra.dailyappkt.models.responses.BlockerResponse
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BlockerService(
    private val blockerRepository: BlockerRepository,
    private val taskRepository: TaskRepository,
    private val subTaskRepository: SubTaskRepository
) {

    fun findAll(): List<BlockerResponse> = blockerRepository.findAll().map { it.toBlockerResponse() }

    fun findById(id: Int): BlockerResponse? =
        blockerRepository.findById(id).map { it.toBlockerResponse() }.orElse(null)

    fun findByTaskId(taskId: Int): List<BlockerResponse> =
        blockerRepository.findByTaskId(taskId).map { it.toBlockerResponse() }

    fun save(taskId: Int, blocker: CreateBlockerRequest): BlockerResponse {
        val task = taskRepository.findById(taskId).orElse(null)
            ?: throw IllegalArgumentException("Task not found")

        val subTask = if (blocker.subTaskId != null) {
            val st = subTaskRepository.findById(blocker.subTaskId).orElse(null)
                ?: throw IllegalArgumentException("SubTask not found")
            // Verify that the subtask belongs to the task
            if (st.task.id != taskId) {
                throw IllegalArgumentException("SubTask does not belong to the specified task")
            }
            st
        } else {
            null
        }

        val newBlocker = Blocker(
            task = task,
            subTask = subTask,
            reason = blocker.reason,
            isResolved = false
        )
        return blockerRepository.save(newBlocker).toBlockerResponse()
    }

    fun replace(id: Int, taskId: Int, blocker: CreateBlockerRequest): BlockerResponse {
        val existingBlocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (existingBlocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        val task = taskRepository.findById(taskId).orElse(null)
            ?: throw IllegalArgumentException("Task not found")

        val subTask = if (blocker.subTaskId != null) {
            val st = subTaskRepository.findById(blocker.subTaskId).orElse(null)
                ?: throw IllegalArgumentException("SubTask not found")
            if (st.task.id != taskId) {
                throw IllegalArgumentException("SubTask does not belong to the specified task")
            }
            st
        } else {
            null
        }

        existingBlocker.task = task
        existingBlocker.subTask = subTask
        existingBlocker.reason = blocker.reason
        return blockerRepository.save(existingBlocker).toBlockerResponse()
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

        if (blocker.subTaskId != null) {
            val st = subTaskRepository.findById(blocker.subTaskId).orElse(null)
                ?: throw IllegalArgumentException("SubTask not found")
            if (st.task.id != taskId) {
                throw IllegalArgumentException("SubTask does not belong to the specified task")
            }
            existingBlocker.subTask = st
        }

        return blockerRepository.save(existingBlocker).toBlockerResponse()
    }

    fun deleteById(id: Int) = blockerRepository.deleteById(id)

    fun resolve(id: Int, taskId: Int): BlockerResponse {
        val blocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (blocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        blocker.isResolved = true
        return blockerRepository.save(blocker).toBlockerResponse()
    }

    fun reopen(id: Int, taskId: Int): BlockerResponse {
        val blocker = blockerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Blocker not found") }

        // Guard: ensure blocker belongs to the task
        if (blocker.task.id != taskId) {
            throw IllegalArgumentException("Blocker does not belong to the specified task")
        }

        blocker.isResolved = false
        return blockerRepository.save(blocker).toBlockerResponse()
    }

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
