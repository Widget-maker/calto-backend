package kr.app.calto.controller.dto.response.blogMember

import kr.app.calto.domain.MemberRole
import kr.app.calto.service.dto.BlogMemberDetail

class BlogMemberResponse(
    val name: String,
    val imageUrl: String,
    val comments: String?,
    val role: MemberRole,
    val joinedAt: String,
) {
    constructor(blogMemberDetail: BlogMemberDetail) : this(
        name = blogMemberDetail.name,
        imageUrl = blogMemberDetail.imageUrl,
        comments = blogMemberDetail.comments,
        role = blogMemberDetail.role,
        joinedAt = blogMemberDetail.joinedAt,
    )
}
