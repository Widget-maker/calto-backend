package kr.app.calto.service

import kr.app.calto.controller.dto.request.blogMember.UpdateMemberRoleRequest
import kr.app.calto.service.dto.BlogMemberDetail

interface BlogMemberService {
    fun getBlogMember(
        userId: Long,
        blogId: Long,
        blogMemberId: Long,
    ): BlogMemberDetail

    fun getBlogMembers(
        userId: Long,
        blogId: Long,
    ): List<BlogMemberDetail>

    fun getMyMemberProfile(
        userId: Long,
        blogId: Long,
    ): BlogMemberDetail

    fun updateMemberRole(
        userId: Long,
        blogId: Long,
        blogMemberId: Long,
        updatedMemberRoleRequest: UpdateMemberRoleRequest,
    )

    fun leaveBlog(
        userId: Long,
        blogId: Long,
    )

    fun deleteBlogMember(
        userId: Long,
        blogId: Long,
        targetMemberId: Long,
    )
}
