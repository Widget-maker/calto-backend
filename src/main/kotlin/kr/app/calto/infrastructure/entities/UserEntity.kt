package kr.app.calto.infrastructure.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.app.calto.domain.AuthProvider
import kr.app.calto.domain.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener::class)
class UserEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column
    var nickname: String,
    @Column
    var profileImageUrl: String?,
    @Column
    var isProfileSet: Boolean = false,
    @Column
    @Enumerated(EnumType.STRING)
    val provider: AuthProvider,
    @Column
    val providerId: String,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column
    var updatedAt: LocalDateTime? = null,
    @Column
    var deletedAt: LocalDateTime? = null,
) {
    fun toDomain() =
        User(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            isProfileSet = isProfileSet,
            provider = provider,
            providerId = providerId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
}
