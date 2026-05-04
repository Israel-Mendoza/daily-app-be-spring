package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.Task
import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.models.requests.CreateTaskRequest
import dev.artisra.dailyappkt.repositories.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.Optional

@Tag("unit")
class TaskServiceTest {

    private val taskRepository = mockk<TaskRepository>()
    private val blockerService = mockk<BlockerService>()
    private val subTaskService = mockk<SubTaskService>()
    private val noteService = mockk<NoteService>()
    private val taskPolicyService = mockk<TaskPolicyService>(relaxed = true)
    
    private val taskService = TaskService(
        taskRepository, blockerService, subTaskService, noteService, taskPolicyService
    )

    @Test
    fun `save should create a new task with TODO status`() {
        val request = CreateTaskRequest(title = "New Task")
        val task = Task(id = 1, title = "New Task", status = TaskStatus.TODO.toString())
        
        every { taskRepository.save(any()) } returns task

        val response = taskService.save(request)

        assertEquals("New Task", response.title)
        assertEquals(TaskStatus.TODO.toString(), response.status)
        verify { taskRepository.save(match { it.title == "New Task" && it.status == TaskStatus.TODO.toString() }) }
    }

    @Test
    fun `startTask should transition task to IN_PROGRESS`() {
        val task = Task(id = 1, title = "Task", status = TaskStatus.TODO.toString())
        every { taskRepository.findById(1) } returns Optional.of(task)
        every { taskRepository.save(any()) } returns task

        taskService.startTask(1)

        assertEquals(TaskStatus.IN_PROGRESS.toString(), task.status)
        verify { taskPolicyService.ensureCanTransitionStatus(task, TaskStatus.IN_PROGRESS) }
        verify { taskRepository.save(task) }
    }

    @Test
    fun `completeTask should transition task to DONE`() {
        val task = Task(id = 1, title = "Task", status = TaskStatus.IN_PROGRESS.toString())
        every { taskRepository.findById(1) } returns Optional.of(task)
        every { taskRepository.save(any()) } returns task

        taskService.completeTask(1)

        assertEquals(TaskStatus.DONE.toString(), task.status)
        verify { taskPolicyService.ensureCanTransitionStatus(task, TaskStatus.DONE) }
        verify { taskRepository.save(task) }
    }
}
