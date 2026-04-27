package dev.artisra.dailyappkt.entities

import dev.artisra.dailyappkt.models.enums.NoteCategory
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "task_notes")
@EntityListeners(AuditingEntityListener::class)
class Note(
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
    var content: String,

    @Column(nullable = false, length = 50)
    var category: String = NoteCategory.GENERAL.name,

    @Column(nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    override fun toString(): String {
        return "TaskNote(id=$id, content='$content', category='$category', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
