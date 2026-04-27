package dev.artisra.dailyappkt

import dev.artisra.dailyappkt.repositories.BlockerRepository
import dev.artisra.dailyappkt.repositories.NoteRepository
import dev.artisra.dailyappkt.repositories.SubTaskRepository
import dev.artisra.dailyappkt.repositories.TaskRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableJpaAuditing
class DailyAppKtApplication

fun main(args: Array<String>) {
    runApplication<DailyAppKtApplication>(*args)
}

@Component
class MyRunner(
    private val blockerRepository: BlockerRepository,
    private val noteRepository: NoteRepository,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        println("Notes:")
        noteRepository.findAll().forEach { blocker ->
            println("Task: ${blocker.content}")
        }
    }
}