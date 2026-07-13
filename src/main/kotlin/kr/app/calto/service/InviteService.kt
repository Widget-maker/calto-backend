package kr.app.calto.service

import kr.app.calto.controller.dto.request.invite.JoinBlogRequest
import kr.app.calto.service.dto.InviteCreatedResult

interface InviteService {
    fun createInviteCode(
        userId: Long,
        blogId: Long,
    ): InviteCreatedResult

    fun getActiveInviteCode(
        userId: Long,
        blogId: Long,
    ): InviteCreatedResult?

    fun deleteInviteCode(
        userId: Long,
        blogId: Long,
        code: String,
    )

    fun joinBlog(
        userId: Long,
        blogId: Long,
        code: String,
        joinBlogRequest: JoinBlogRequest,
    )
}
