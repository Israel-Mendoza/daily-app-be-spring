package dev.artisra.dailyappkt.repositories

import dev.artisra.dailyappkt.entities.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Int> {
}