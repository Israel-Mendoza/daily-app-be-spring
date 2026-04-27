package dev.artisra.dailyappkt.services

import dev.artisra.dailyappkt.entities.DailySession
import dev.artisra.dailyappkt.repositories.DailySessionRepository
import org.springframework.stereotype.Service

@Service
class DailySessionService(private val dailySessionRepository: DailySessionRepository) {

    fun findAll(): List<DailySession> = dailySessionRepository.findAll()

    fun findById(id: Int): DailySession? = dailySessionRepository.findById(id).orElse(null)

    fun save(dailySession: DailySession): DailySession = dailySessionRepository.save(dailySession)

    fun deleteById(id: Int) = dailySessionRepository.deleteById(id)
}
