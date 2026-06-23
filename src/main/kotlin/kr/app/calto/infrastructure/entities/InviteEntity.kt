package kr.app.calto.infrastructure.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "invite",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_invite_code", columnNames = ["code"]),
    ],
)
@EntityListeners(AuditingEntityListener::class)
class InviteEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val blogId: Long,
    @Column(nullable = false, length = 64)
    val code: String,
    @Column(nullable = false, name = "invite_user_id")
    val inviteUserId: Long,
    @Column(name = "used_user_id")
    var usedUserId: Long? = null,
    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null,
    @Column(nullable = false, name = "expires_at")
    val expiresAt: LocalDateTime,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
