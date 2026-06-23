package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.BlogMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BlogMemberRepository : JpaRepository<BlogMemberEntity, Long> {
    fun findByBlogIdAndId(
        blogId: Long,
        id: Long,
    ): BlogMemberEntity?

    fun findByBlogId(blogId: Long): List<BlogMemberEntity>

    fun findByBlogIdAndUserId(
        blogId: Long,
        userId: Long,
    ): BlogMemberEntity?

    fun existsByBlogIdAndUserId(
        blogId: Long,
        userId: Long,
    ): Boolean
}
