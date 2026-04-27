package dev.artisra.dailyappkt.repositories

import dev.artisra.dailyappkt.entities.Blocker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlockerRepository : JpaRepository<Blocker, Int> {
    fun findByTaskId(taskId: Int): List<Blocker>
    fun findByTaskIdAndSubTaskId(taskId: Int, subTaskId: Int?): List<Blocker>
}