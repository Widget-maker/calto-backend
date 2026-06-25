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
import kr.app.calto.domain.BackgroundType
import kr.app.calto.domain.Blog
import kr.app.calto.domain.BlogColor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "blog")
@EntityListeners(AuditingEntityListener::class)
class BlogEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column
    var name: String,
    @Column
    var members: Int,
    @Column
    var imageUrl: String,
    @Column
    @Enumerated(EnumType.STRING)
    var mainColor: BlogColor,
    @Column
    var backgroundImageUrl: String? = null,
    @Column
    @Enumerated(EnumType.STRING)
    var backgroundType: BackgroundType = BackgroundType.COLOR,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column
    var updatedAt: LocalDateTime? = null,
    @Column
    var deletedAt: LocalDateTime? = null,
) {
    fun toDomain() =
        Blog(
            name = name,
            imageUrl = imageUrl,
            members = members,
            mainColor = mainColor,
            backgroundImageUrl = backgroundImageUrl,
            backgroundType = backgroundType,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
}
