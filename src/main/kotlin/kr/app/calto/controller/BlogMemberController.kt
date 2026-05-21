package kr.app.calto.controller

import kr.app.calto.controller.dto.request.blogMember.UpdateMemberRoleRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.blogMember.BlogMemberResponse
import kr.app.calto.service.BlogMemberService
import org.springframework.http.ResponseEntity
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
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<List<BlogMemberResponse>>> =
        runCatching {
            blogMemberService.getBlogMembers(blogId)
        }.fold(
            onSuccess = { members -> ResponseEntity.ok(ApiResponse.success(members.map { BlogMemberResponse(it) })) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @GetMapping("/{blogMemberId}")
    fun getMember(
        @PathVariable blogId: Long,
        @PathVariable blogMemberId: Long,
    ): ResponseEntity<ApiResponse<BlogMemberResponse>> =
        runCatching {
            blogMemberService.getBlogMember(blogId, blogMemberId)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success(BlogMemberResponse(it))) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @PutMapping("/{blogMemberId}")
    fun updateMemberRole(
        @PathVariable blogId: Long,
        @PathVariable blogMemberId: Long,
        @RequestBody updateMemberRoleRequest: UpdateMemberRoleRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogMemberService.updateMemberRole(blogId, blogMemberId, updateMemberRoleRequest)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success()) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @DeleteMapping("/leave/{blogMemberId}")
    fun leaveBlogMember(
        @PathVariable blogId: Long,
        @PathVariable blogMemberId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogMemberService.leaveBlog(blogId, blogMemberId)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success()) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @DeleteMapping("/delete/{targetMemberId}")
    fun deleteTargetMember(
        @PathVariable blogId: Long,
        @PathVariable targetMemberId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogMemberService.deleteBlogMember(blogId, targetMemberId)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success()) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )
}
