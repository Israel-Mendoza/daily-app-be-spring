package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Blocker
import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.exceptions.OpenBlockersException
import dev.artisra.dailyappkt.exceptions.OpenSubTasksException
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Tag("unit")
class TaskPolicyServiceTest {

    private val blockerRepository = mockk<BlockerRepository>()
    private val subTaskRepository = mockk<SubTaskRepository>()
    private val taskPolicyService = TaskPolicyService(blockerRepository, subTaskRepository)

    @Test
    fun `should throw OpenBlockersException when transitioning from BLOCKED with unresolved blockers`() {
        val task = Task(id = 1, title = "Test Task", status = TaskStatus.BLOCKED.toString())
        val blocker = Blocker(id = 1, task = task, reason = "Blocker", isResolved = false)
        
        every { blockerRepository.findByTaskId(1) } returns listOf(blocker)

        assertThrows<OpenBlockersException> {
            taskPolicyService.ensureCanTransitionStatus(task, TaskStatus.IN_PROGRESS)
        }
    }

    @Test
    fun `should not throw when transitioning from BLOCKED with all blockers resolved`() {
        val task = Task(id = 1, title = "Test Task", status = TaskStatus.BLOCKED.toString())
        val blocker = Blocker(id = 1, task = task, reason = "Blocker", isResolved = true)
        
        every { blockerRepository.findByTaskId(1) } returns listOf(blocker)
        every { subTaskRepository.findByTaskId(1) } returns emptyList()

        assertDoesNotThrow {
            taskPolicyService.ensureCanTransitionStatus(task, TaskStatus.IN_PROGRESS)
        }
    }

    @Test
    fun `should throw OpenSubTasksException when completing task with incomplete subtasks`() {
        val task = Task(id = 1, title = "Test Task", status = TaskStatus.IN_PROGRESS.toString())
        val subTask = SubTask(id = 1, task = task, title = "SubTask", isCompleted = false)
        
        every { subTaskRepository.findByTaskId(1) } returns listOf(subTask)

        assertThrows<OpenSubTasksException> {
            taskPolicyService.ensureCanTransitionStatus(task, TaskStatus.DONE)
        }
    }

    @Test
    fun `should allow CANCELED even with unresolved blockers`() {
        val task = Task(id = 1, title = "Test Task", status = TaskStatus.BLOCKED.toString())
        
        every { subTaskRepository.findByTaskId(1) } returns emptyList()

        assertDoesNotThrow {
            taskPolicyService.ensureCanTransitionStatus(task, TaskStatus.CANCELED)
        }
    }
}
