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
import kr.app.calto.domain.BlogMember
import kr.app.calto.domain.MemberRole
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "blog_member")
@EntityListeners(AuditingEntityListener::class)
class BlogMemberEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column
    val blogId: Long,
    @Column
    val userId: Long,
    @Column
    var name: String,
    @Column
    var imageUrl: String,
    @Column
    var comments: String? = null,
    @Column
    @Enumerated(EnumType.STRING)
    var role: MemberRole,
    @CreatedDate
    val createdAt: LocalDateTime,
    @Column
    var updatedAt: LocalDateTime?,
    @Column
    var deletedAt: LocalDateTime?,
) {
    fun toDomain() =
        BlogMember(
            name = name,
            imageUrl = imageUrl,
            comments = comments,
            role = role,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
}
