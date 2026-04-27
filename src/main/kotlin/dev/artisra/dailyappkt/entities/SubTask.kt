package dev.artisra.dailyappkt.entities

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "sub_tasks")
@EntityListeners(AuditingEntityListener::class)
class SubTask(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: Task,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(nullable = false)
    var isCompleted: Boolean = false,

    @Column(nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    override fun toString(): String {
        return "SubTask(id=$id, title='$title', isCompleted=$isCompleted, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
