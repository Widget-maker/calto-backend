package kr.app.calto.infrastructure.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_token")
@EntityListeners(AuditingEntityListener::class)
class RefreshTokenEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column
    val userId: Long,
    @Column
    val token: String,
    @Column
    val expiresAt: LocalDateTime,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
