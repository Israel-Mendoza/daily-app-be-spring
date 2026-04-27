package dev.artisra.dailyappkt.repositories

import dev.artisra.dailyappkt.entities.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : JpaRepository<Note, Int> {
    fun findByTaskId(taskId: Int): List<Note>
}