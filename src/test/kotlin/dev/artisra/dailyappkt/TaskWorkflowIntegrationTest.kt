package dev.artisra.dailyappkt

import dev.artisra.dailyappkt.models.enums.TaskStatus
import dev.artisra.dailyappkt.models.requests.CreateBlockerRequest
import dev.artisra.dailyappkt.models.requests.CreateTaskRequest
import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import dev.artisra.dailyappkt.services.BlockerService
import dev.artisra.dailyappkt.services.SubTaskService
import dev.artisra.dailyappkt.services.TaskService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class TaskWorkflowIntegrationTest {

    @Autowired
    lateinit var taskService: TaskService

    @Autowired
    lateinit var subTaskService: SubTaskService

    @Autowired
    lateinit var blockerService: BlockerService

    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var subTaskRepository: SubTaskRepository

    @Autowired
    lateinit var blockerRepository: BlockerRepository

    @BeforeEach
    fun setup() {
        blockerRepository.deleteAll()
        subTaskRepository.deleteAll()
        taskRepository.deleteAll()
    }

    @Test
    fun `should follow full task workflow with blockers`() {
        // 1. Create Task
        val taskResponse = taskService.save(CreateTaskRequest(title = "Task 1"))
        val taskId = taskResponse.id

        // 2. Start Task
        taskService.startTask(taskId)
        assertEquals(TaskStatus.IN_PROGRESS.toString(), taskRepository.findById(taskId).get().status)

        // 3. Create Blocker -> Should move to BLOCKED
        blockerService.save(taskId, CreateBlockerRequest(reason = "Wait for info", subTaskId = null))
        assertEquals(TaskStatus.BLOCKED.toString(), taskRepository.findById(taskId).get().status)

        // 4. Resolve Blocker -> Should move back to IN_PROGRESS
        val blockerId = blockerRepository.findByTaskId(taskId).first().id!!
        blockerService.resolve(blockerId, taskId)
        assertEquals(TaskStatus.IN_PROGRESS.toString(), taskRepository.findById(taskId).get().status)

        // 5. Complete Task -> Should move to DONE
        taskService.completeTask(taskId)
        assertEquals(TaskStatus.DONE.toString(), taskRepository.findById(taskId).get().status)
    }

    @Test
    fun `should reopen task and subtask when blocker is reopened`() {
        // 1. Setup: Done task with completed subtask and resolved blocker
        val taskId = taskService.save(CreateTaskRequest(title = "Task 1")).id
        taskService.startTask(taskId)
        
        val subTaskId = subTaskService.save(taskId, dev.artisra.dailyappkt.models.requests.CreateAndUpdateSubTaskRequest(title = "Sub 1")).id!!
        subTaskService.complete(taskId, subTaskId)
        
        val blockerId = blockerService.save(taskId, CreateBlockerRequest(reason = "B1", subTaskId = subTaskId)).id!!
        blockerService.resolve(blockerId, taskId)
        
        // RE-COMPLETE subtask after resolving blocker, because resolving/saving blocker might have triggered sync
        subTaskService.complete(taskId, subTaskId)
        
        taskService.completeTask(taskId)
        
        assertEquals(TaskStatus.DONE.toString(), taskRepository.findById(taskId).get().status)
        assertTrue(subTaskRepository.findById(subTaskId).get().isCompleted)

        // 2. Reopen Blocker
        blockerService.reopen(blockerId, taskId)

        // 3. Verify: Task should be BLOCKED and SubTask should be NOT completed
        assertEquals(TaskStatus.BLOCKED.toString(), taskRepository.findById(taskId).get().status)
        assertEquals(false, subTaskRepository.findById(subTaskId).get().isCompleted)
    }
}
