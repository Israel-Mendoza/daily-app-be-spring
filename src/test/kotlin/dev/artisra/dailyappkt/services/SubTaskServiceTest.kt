package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.SubTask
import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.requests.CreateAndUpdateSubTaskRequest
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.Optional
import java.time.LocalDateTime

@Tag("unit")
class SubTaskServiceTest {

    private val subTaskRepository = mockk<SubTaskRepository>()
    private val taskRepository = mockk<TaskRepository>()
    private val taskSynchronizerService = mockk<TaskSynchronizerService>(relaxed = true)
    private val subTaskPolicyService = mockk<SubTaskPolicyService>(relaxed = true)

    private val subTaskService = SubTaskService(
        subTaskRepository, taskRepository, taskSynchronizerService, subTaskPolicyService
    )

    @Test
    fun `save should sync task and create subtask`() {
        val task = Task(id = 1, title = "Task")
        val request = CreateAndUpdateSubTaskRequest(title = "SubTask")
        val subTask = SubTask(id = 1, task = task, title = "SubTask", isCompleted = false, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { taskRepository.findById(1) } returns Optional.of(task)
        every { subTaskRepository.save(any()) } returns subTask

        subTaskService.save(1, request)

        verify { taskSynchronizerService.syncTaskWithNewSubTasks(1) }
        verify { subTaskRepository.save(match { it.title == "SubTask" && it.task.id == 1 }) }
    }

    @Test
    fun `complete should update status`() {
        val task = Task(id = 1, title = "Task")
        val subTask = SubTask(id = 2, task = task, title = "SubTask", isCompleted = false, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { subTaskRepository.findById(2) } returns Optional.of(subTask)
        every { subTaskRepository.save(any()) } returns subTask

        subTaskService.complete(1, 2)

        assertEquals(true, subTask.isCompleted)
        verify { subTaskPolicyService.ensureCanCompleteSubTask(subTask) }
        verify { subTaskRepository.save(subTask) }
    }

    @Test
    fun `reopen should update status and sync task`() {
        val task = Task(id = 1, title = "Task")
        val subTask = SubTask(id = 2, task = task, title = "SubTask", isCompleted = true, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { subTaskRepository.findById(2) } returns Optional.of(subTask)
        every { subTaskRepository.save(any()) } returns subTask

        subTaskService.reopen(1, 2)

        assertEquals(false, subTask.isCompleted)
        verify { subTaskRepository.save(subTask) }
        verify { taskSynchronizerService.syncTaskWithNewSubTasks(1) }
    }
}
