package dev.artisra.dailyappkt.entities

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "daily_sessions")
@EntityListeners(AuditingEntityListener::class)
class DailySession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "session_date", nullable = false)
    var sessionDate: LocalDate,

    @Column(name = "raw_notes_blob", nullable = false)
    var rawNotesBlob: String,

    @Column(nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    var generatedScript: String
) {
    override fun toString(): String {
        return "DailySession(id=$id, sessionDate=$sessionDate, createdAt=$createdAt)"
    }
}
