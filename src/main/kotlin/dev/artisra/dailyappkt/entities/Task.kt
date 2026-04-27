package dev.artisra.dailyappkt.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener::class)
class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(nullable = false, length = 50)
    var status: String = "TODO",

    @Column(nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    override fun toString(): String {
        return "Task(id=$id, title='$title', status='$status', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}