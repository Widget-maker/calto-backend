package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.BlogMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BlogMemberRepository : JpaRepository<BlogMemberEntity, Long> {
    fun findByBlogIdAndId(
        blogId: Long,
        id: Long,
    ): BlogMemberEntity?

    fun findByBlogId(blogId: Long): List<BlogMemberEntity>

    fun findByBlogIdAndUserIdAndDeletedAtIsNull(
        blogId: Long,
        userId: Long,
    ): BlogMemberEntity?

    fun existsByBlogIdAndUserIdAndDeletedAtIsNull(
        blogId: Long,
        userId: Long,
    ): Boolean
}
