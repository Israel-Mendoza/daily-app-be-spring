package dev.artisra.dailyappkt.repositories

import dev.artisra.dailyappkt.entities.SubTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubTaskRepository : JpaRepository<SubTask, Int> {
    fun findByTaskId(taskId: Int): List<SubTask>
}