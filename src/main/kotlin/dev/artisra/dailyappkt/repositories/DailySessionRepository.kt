package dev.artisra.dailyappkt.repositories

import dev.artisra.dailyappkt.entities.DailySession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailySessionRepository : JpaRepository<DailySession, Int> {
}