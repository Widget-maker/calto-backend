package kr.app.calto.controller

import kr.app.calto.controller.dto.request.blogMember.UpdateMemberRoleRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.Responses
import kr.app.calto.controller.dto.response.blogMember.BlogMemberResponse
import kr.app.calto.service.BlogMemberService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blogs/{blogId}/members")
class BlogMemberController(
    private val blogMemberService: BlogMemberService,
) {
    @GetMapping
    fun getMembers(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<List<BlogMemberResponse>>> =
        runCatching {
            blogMemberService.getBlogMembers(userId, blogId)
        }.fold(
            onSuccess = { Responses.success(it.map { BlogMemberResponse(it) }) },
            onFailure = { Responses.failure(it) },
        )

    @GetMapping("/{blogMemberId}")
    fun getMember(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @PathVariable blogMemberId: Long,
    ): ResponseEntity<ApiResponse<BlogMemberResponse>> =
        runCatching {
            blogMemberService.getBlogMember(userId, blogId, blogMemberId)
        }.fold(
            onSuccess = { Responses.success(BlogMemberResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @PutMapping("/{blogMemberId}")
    fun updateMemberRole(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @PathVariable blogMemberId: Long,
        @RequestBody updateMemberRoleRequest: UpdateMemberRoleRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogMemberService.updateMemberRole(userId, blogId, blogMemberId, updateMemberRoleRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @DeleteMapping("/leave/{blogMemberId}")
    fun leaveBlogMember(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogMemberService.leaveBlog(userId, blogId)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @DeleteMapping("/delete/{targetMemberId}")
    fun deleteTargetMember(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @PathVariable targetMemberId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogMemberService.deleteBlogMember(userId, blogId, targetMemberId)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )
}
