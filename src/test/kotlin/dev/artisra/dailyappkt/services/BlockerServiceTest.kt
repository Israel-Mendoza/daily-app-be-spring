package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Blocker
import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.requests.CreateBlockerRequest
import dev.artisra.dailyappkt.repositories.BlockerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.Optional
import java.time.LocalDateTime

@Tag("unit")
class BlockerServiceTest {

    private val blockerRepository = mockk<BlockerRepository>()
    private val taskOwnershipGuardService = mockk<TaskOwnershipGuardService>()
    private val taskSynchronizerService = mockk<TaskSynchronizerService>(relaxed = true)
    private val subTaskSynchronizerService = mockk<SubTaskSynchronizerService>(relaxed = true)

    private val blockerService = BlockerService(
        blockerRepository, taskOwnershipGuardService, taskSynchronizerService, subTaskSynchronizerService
    )

    @Test
    fun `save should create blocker and sync task and subtask`() {
        val task = Task(id = 1, title = "Task")
        val subTask = SubTask(id = 2, task = task, title = "SubTask", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        val request = CreateBlockerRequest(reason = "Blocker", subTaskId = 2)
        val blocker = Blocker(id = 3, task = task, subTask = subTask, reason = "Blocker", isResolved = false, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { taskOwnershipGuardService.ensureTaskExists(1) } returns task
        every { taskOwnershipGuardService.ensureSubTaskBelongsToTask(1, 2) } returns subTask
        every { blockerRepository.save(any()) } returns blocker

        blockerService.save(1, request)

        verify { blockerRepository.save(match { it.reason == "Blocker" && it.task.id == 1 && it.subTask?.id == 2 }) }
        verify { taskSynchronizerService.syncTaskWithBlockers(1) }
        verify { subTaskSynchronizerService.syncSubTaskWithBlockers(1, 2) }
    }

    @Test
    fun `resolve should update blocker and sync task and subtask`() {
        val task = Task(id = 1, title = "Task")
        val subTask = SubTask(id = 2, task = task, title = "SubTask", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        val blocker = Blocker(id = 3, task = task, subTask = subTask, reason = "Blocker", isResolved = false, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { blockerRepository.findById(3) } returns Optional.of(blocker)
        every { blockerRepository.save(any()) } returns blocker

        blockerService.resolve(3, 1)

        assertEquals(true, blocker.isResolved)
        verify { blockerRepository.save(blocker) }
        verify { taskSynchronizerService.syncTaskWithBlockers(1) }
        verify { subTaskSynchronizerService.syncSubTaskWithBlockers(1, 2) }
    }

    @Test
    fun `reopen should update blocker and sync task and subtask`() {
        val task = Task(id = 1, title = "Task")
        val subTask = SubTask(id = 2, task = task, title = "SubTask", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        val blocker = Blocker(id = 3, task = task, subTask = subTask, reason = "Blocker", isResolved = true, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { blockerRepository.findById(3) } returns Optional.of(blocker)
        every { blockerRepository.save(any()) } returns blocker

        blockerService.reopen(3, 1)

        assertEquals(false, blocker.isResolved)
        verify { blockerRepository.save(blocker) }
        verify { taskSynchronizerService.syncTaskWithBlockers(1) }
        verify { subTaskSynchronizerService.syncSubTaskWithBlockers(1, 2) }
    }
}
