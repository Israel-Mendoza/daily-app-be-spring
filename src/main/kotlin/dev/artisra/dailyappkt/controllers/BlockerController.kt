package dev.artisra.dailyappkt.controllers

import dev.artisra.dailyappkt.models.requests.CreateBlockerRequest
import dev.artisra.dailyappkt.models.requests.UpdateBlockerRequest
import dev.artisra.dailyappkt.models.responses.BlockerResponse
import dev.artisra.dailyappkt.services.BlockerService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks/{taskId}/blockers")
class BlockerController(
    private val blockerService: BlockerService
) {

    @GetMapping
    fun getBlockersForTask(@PathVariable taskId: Int): ResponseEntity<List<BlockerResponse>> {
        log.info("Getting all blockers for task $taskId")
        val blockers = blockerService.findByTaskId(taskId)
        return if (blockers.isEmpty()) ResponseEntity.noContent().build() else ResponseEntity.ok(blockers)
    }

    @GetMapping("/{blockerId}")
    fun getBlocker(@PathVariable taskId: Int, @PathVariable blockerId: Int): ResponseEntity<BlockerResponse> {
        log.info("Getting blocker $blockerId for task $taskId")
        val blocker = blockerService.findById(blockerId) ?: return ResponseEntity.notFound().build()

        // Guard: ensure blocker belongs to the task
        if (blocker.taskId != taskId) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(blocker)
    }

    @PostMapping
    fun createBlocker(
        @PathVariable taskId: Int,
        @RequestBody request: CreateBlockerRequest
    ): ResponseEntity<BlockerResponse> {
        log.info("Creating blocker for task $taskId")
        return ResponseEntity.ok(blockerService.save(taskId, request))
    }

    @PutMapping("/{blockerId}")
    fun replaceBlocker(
        @PathVariable taskId: Int,
        @PathVariable blockerId: Int,
        @RequestBody request: CreateBlockerRequest
    ): ResponseEntity<BlockerResponse> {
        log.info("Replacing blocker $blockerId for task $taskId")
        return ResponseEntity.ok(blockerService.replace(blockerId, taskId, request))
    }

    @PatchMapping("/{blockerId}")
    fun updateBlocker(
        @PathVariable taskId: Int,
        @PathVariable blockerId: Int,
        @RequestBody request: UpdateBlockerRequest
    ): ResponseEntity<BlockerResponse> {
        log.info("Updating blocker $blockerId for task $taskId")
        return ResponseEntity.ok(blockerService.update(blockerId, taskId, request))
    }

    @DeleteMapping("/{blockerId}")
    fun deleteBlocker(@PathVariable taskId: Int, @PathVariable blockerId: Int): ResponseEntity<Unit> {
        log.info("Deleting blocker $blockerId for task $taskId")
        val blocker = blockerService.findById(blockerId) ?: return ResponseEntity.notFound().build()

        // Guard: ensure blocker belongs to the task
        if (blocker.taskId != taskId) {
            return ResponseEntity.notFound().build()
        }

        blockerService.deleteById(blockerId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{blockerId}/resolve")
    fun resolveBlocker(@PathVariable taskId: Int, @PathVariable blockerId: Int): ResponseEntity<BlockerResponse> {
        log.info("Resolving blocker $blockerId for task $taskId")
        return ResponseEntity.ok(blockerService.resolve(blockerId, taskId))
    }

    @PostMapping("/{blockerId}/reopen")
    fun reopenBlocker(@PathVariable taskId: Int, @PathVariable blockerId: Int): ResponseEntity<BlockerResponse> {
        log.info("Reopening blocker $blockerId for task $taskId")
        return ResponseEntity.ok(blockerService.reopen(blockerId, taskId))
    }

    companion object {
        private val log = LoggerFactory.getLogger(BlockerController::class.java)
    }
}

