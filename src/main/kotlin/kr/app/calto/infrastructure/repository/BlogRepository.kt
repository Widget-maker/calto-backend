package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.BlogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BlogRepository : JpaRepository<BlogEntity, Long> {
    // DDD 관점에선 분리가 적절하지만 성능을 우선적으로 고려
    @Query(
        """
        SELECT b FROM BlogEntity b
        JOIN BlogMemberEntity m ON m.blogId = b.id
        WHERE m.userId = :userId
          AND m.deletedAt IS NULL
          AND b.deletedAt IS NULL
        """,
    )
    fun findAllByMemberUserId(
        @Param("userId") userId: Long,
    ): List<BlogEntity>
}
