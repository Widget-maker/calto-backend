package kr.app.calto.service.dto

import kr.app.calto.domain.BlogMember
import kr.app.calto.domain.MemberRole
import java.time.format.DateTimeFormatter

class BlogMemberDetail(
    val name: String,
    val imageUrl: String,
    val comments: String?,
    val role: MemberRole,
    val joinedAt: String,
) {
    companion object {
        fun from(blogMember: BlogMember): BlogMemberDetail =
            BlogMemberDetail(
                name = blogMember.name,
                imageUrl = blogMember.imageUrl,
                comments = blogMember.comments,
                role = blogMember.role,
                joinedAt = blogMember.createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")),
            )
    }
}