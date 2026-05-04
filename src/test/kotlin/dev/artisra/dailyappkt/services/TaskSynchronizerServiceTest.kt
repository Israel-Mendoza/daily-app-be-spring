package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Blocker
import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.Optional
import kotlin.test.assertEquals

@Tag("unit")
class TaskSynchronizerServiceTest {

    private val blockerRepository = mockk<BlockerRepository>()
    private val subTaskRepository = mockk<SubTaskRepository>()
    private val taskRepository = mockk<TaskRepository>()
    private val taskSynchronizerService = TaskSynchronizerService(blockerRepository, subTaskRepository, taskRepository)

    @Test
    fun `syncTaskWithBlockers should move task to BLOCKED if it has unresolved blockers`() {
        val task = Task(id = 1, title = "Task", status = TaskStatus.IN_PROGRESS.toString())
        val blocker = Blocker(id = 1, task = task, reason = "B", isResolved = false)

        every { taskRepository.findById(1) } returns Optional.of(task)
        every { blockerRepository.findByTaskId(1) } returns listOf(blocker)
        every { taskRepository.save(any()) } returns task

        taskSynchronizerService.syncTaskWithBlockers(1)

        assertEquals(TaskStatus.BLOCKED.toString(), task.status)
        verify { taskRepository.save(task) }
    }

    @Test
    fun `syncTaskWithBlockers should move task to IN_PROGRESS if all blockers are resolved and it was BLOCKED`() {
        val task = Task(id = 1, title = "Task", status = TaskStatus.BLOCKED.toString())
        val blocker = Blocker(id = 1, task = task, reason = "B", isResolved = true)

        every { taskRepository.findById(1) } returns Optional.of(task)
        every { blockerRepository.findByTaskId(1) } returns listOf(blocker)
        every { taskRepository.save(any()) } returns task

        taskSynchronizerService.syncTaskWithBlockers(1)

        assertEquals(TaskStatus.IN_PROGRESS.toString(), task.status)
        verify { taskRepository.save(task) }
    }

    @Test
    fun `syncTaskWithBlockers should keep task DONE if all blockers are resolved`() {
        val task = Task(id = 1, title = "Task", status = TaskStatus.DONE.toString())
        val blocker = Blocker(id = 1, task = task, reason = "B", isResolved = true)

        every { taskRepository.findById(1) } returns Optional.of(task)
        every { blockerRepository.findByTaskId(1) } returns listOf(blocker)
        every { taskRepository.save(any()) } returns task

        taskSynchronizerService.syncTaskWithBlockers(1)

        assertEquals(TaskStatus.DONE.toString(), task.status)
        verify { taskRepository.save(task) }
    }

    @Test
    fun `syncTaskWithNewSubTasks should move task to IN_PROGRESS if it was DONE`() {
        val task = Task(id = 1, title = "Task", status = TaskStatus.DONE.toString())

        every { taskRepository.findById(1) } returns Optional.of(task)
        every { taskRepository.save(any()) } returns task

        taskSynchronizerService.syncTaskWithNewSubTasks(1)

        assertEquals(TaskStatus.IN_PROGRESS.toString(), task.status)
        verify { taskRepository.save(task) }
    }
}
