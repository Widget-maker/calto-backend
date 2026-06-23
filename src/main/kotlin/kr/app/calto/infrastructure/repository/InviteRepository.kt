package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.InviteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface InviteRepository : JpaRepository<InviteEntity, Long> {
    fun findByBlogIdAndCode(
        blogId: Long,
        code: String,
    ): InviteEntity?

    @Query(
        """
        SELECT i FROM InviteEntity i
        WHERE i.blogId = :blogId
          AND i.inviteUserId = :userId
          AND i.usedUserId IS NULL
          AND i.expiresAt > :now
        """,
    )
    fun findActiveByCreator(
        @Param("blogId") blogId: Long,
        @Param("userId") userId: Long,
        @Param("now") now: LocalDateTime,
    ): InviteEntity?
}
