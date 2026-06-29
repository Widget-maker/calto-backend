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

    // 유저가 소속된 블로그 갯수
    fun countByUserIdAndDeletedAtIsNull(userId: Long): Long

    // 특정 블로그의 멤버 수
    fun countByBlogIdAndDeletedAtIsNull(blogId: Long): Long
}
