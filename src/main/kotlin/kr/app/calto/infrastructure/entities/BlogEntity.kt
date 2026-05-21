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
    // TODO: 배경화면 이미지 사용시 image url 필드 필요,
    //       제공되는 디폴트 컬러 사용유무 boolean 판단 필드 필요
    @Column
    var members: Int,
    @Column
    var imageUrl: String,
    @Column
    @Enumerated(EnumType.STRING)
    var mainColor: BlogColor,
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
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
}
