package kr.app.calto.controller

import kr.app.calto.controller.dto.request.invite.JoinBlogRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.Responses
import kr.app.calto.controller.dto.response.invite.InviteUrlResponse
import kr.app.calto.service.InviteService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blogs/{blogId}/invites")
class InviteController(
    private val inviteService: InviteService,
) {
    @PostMapping
    fun createInviteCode(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<InviteUrlResponse>> =
        runCatching {
            inviteService.createInviteCode(userId, blogId)
        }.fold(
            onSuccess = { Responses.success(InviteUrlResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @GetMapping
    fun getActiveInviteCode(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<InviteUrlResponse>> =
        runCatching {
            inviteService.getActiveInviteCode(userId, blogId)
        }.fold(
            onSuccess = { Responses.success(it?.let { InviteUrlResponse(it) }) },
            onFailure = { Responses.failure(it) },
        )

    @DeleteMapping("/{code}")
    fun deleteInviteCode(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            inviteService.deleteInviteCode(userId, blogId, code)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @PostMapping("/{code}/join")
    fun joinBlog(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @PathVariable code: String,
        @RequestBody joinBlogRequest: JoinBlogRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            inviteService.joinBlog(userId, blogId, code, joinBlogRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )
}
