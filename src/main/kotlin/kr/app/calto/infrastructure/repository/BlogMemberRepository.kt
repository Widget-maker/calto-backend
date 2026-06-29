package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.BlogMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BlogMemberRepository : JpaRepository<BlogMemberEntity, Long> {
    fun findByBlogIdAndIdAndDeletedAtIsNull(
        blogId: Long,
        id: Long,
    ): BlogMemberEntity?

    fun findByBlogIdAndDeletedAtIsNull(blogId: Long): List<BlogMemberEntity>

    fun findByBlogIdAndUserIdAndDeletedAtIsNull(
        blogId: Long,
        userId: Long,
    ): BlogMemberEntity?

    fun existsByBlogIdAndUserIdAndDeletedAtIsNull(
        blogId: Long,
        userId: Long,
    ): Boolean

    // 활성 상태로 소속된 블로그 멤버 수 (블로그 개수 제약 검증에 사용)
    fun countByUserIdAndDeletedAtIsNull(userId: Long): Long
}
