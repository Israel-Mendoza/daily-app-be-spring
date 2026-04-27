package dev.artisra.dailyappkt.entities

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "blockers")
@EntityListeners(AuditingEntityListener::class)
class Blocker(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: Task,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_task_id")
    var subTask: SubTask? = null,

    @Column(nullable = false)
    var reason: String,

    @Column(nullable = false)
    var isResolved: Boolean = false,

    @Column(nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    override fun toString(): String {
        return "Blocker(id=$id, reason='$reason', isResolved=$isResolved, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
