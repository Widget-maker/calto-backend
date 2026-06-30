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

    // 해당 블로그 전체 닉네임 중복 검증용
    fun existsByBlogIdAndNameAndDeletedAtIsNull(
        blogId: Long,
        name: String,
    ): Boolean

    // 해당 블로그 본인을 제외한 닉네임 중복 검증용
    fun existsByBlogIdAndNameAndIdNotAndDeletedAtIsNull(
        blogId: Long,
        name: String,
        id: Long,
    ): Boolean
}
