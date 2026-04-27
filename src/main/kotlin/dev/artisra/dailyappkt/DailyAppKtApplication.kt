package dev.artisra.dailyappkt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class DailyAppKtApplication

fun main(args: Array<String>) {
    runApplication<DailyAppKtApplication>(*args)
}
