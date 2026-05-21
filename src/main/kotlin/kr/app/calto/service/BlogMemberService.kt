package kr.app.calto.service

import kr.app.calto.controller.dto.request.blogMember.UpdateMemberRoleRequest
import kr.app.calto.service.dto.BlogMemberDetail

interface BlogMemberService {
    fun getBlogMember(
        blogId: Long,
        blogMemberId: Long,
    ): BlogMemberDetail

    fun getBlogMembers(blogId: Long): List<BlogMemberDetail>

    fun updateMemberRole(
        blogId: Long,
        blogMemberId: Long,
        updatedMemberRoleRequest: UpdateMemberRoleRequest,
    )

    fun leaveBlog(
        blogId: Long,
        blogMemberId: Long,
    )

    fun deleteBlogMember(
        blogId: Long,
        targetMemberId: Long,
    )
}
