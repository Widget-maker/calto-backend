package kr.app.calto.controller.dto.response.blogMember

import kr.app.calto.domain.MemberRole
import kr.app.calto.service.dto.BlogMemberDetail

// TODO: 단건 조회, list 조회, 본인 조회 response 분리에 대해 고려
class BlogMemberResponse(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val comments: String?,
    val role: MemberRole,
    val joinedAt: String,
) {
    constructor(blogMemberDetail: BlogMemberDetail) : this(
        id = blogMemberDetail.id,
        name = blogMemberDetail.name,
        imageUrl = blogMemberDetail.imageUrl,
        comments = blogMemberDetail.comments,
        role = blogMemberDetail.role,
        joinedAt = blogMemberDetail.joinedAt,
    )
}
