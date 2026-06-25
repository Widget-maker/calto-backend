package kr.app.calto.controller

import kr.app.calto.controller.dto.request.blog.CreateBlogRequest
import kr.app.calto.controller.dto.request.blog.UpdateBackgroundImageRequest
import kr.app.calto.controller.dto.request.blog.UpdateBackgroundMainColorRequest
import kr.app.calto.controller.dto.request.blog.UpdateBlogRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.Responses
import kr.app.calto.controller.dto.response.blog.BlogResponse
import kr.app.calto.service.BlogService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blogs")
class BlogController(
    private val blogService: BlogService,
) {
    @GetMapping
    fun getBlogs(): ResponseEntity<ApiResponse<List<BlogResponse>>> =
        runCatching {
            blogService.getAllBlogs()
        }.fold(
            onSuccess = { Responses.success(it.map { BlogResponse(it) }) },
            onFailure = { Responses.failure(it) },
        )

    @GetMapping("/{blogId}")
    fun getBlog(
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<BlogResponse>> =
        runCatching {
            blogService.getBlogById(blogId)
        }.fold(
            onSuccess = { Responses.success(BlogResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @PostMapping
    fun create(
        @RequestBody createBlogRequest: CreateBlogRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.createBlog(createBlogRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @PutMapping("/{blogId}")
    fun update(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @RequestBody updateBlogRequest: UpdateBlogRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.updateBlog(userId, blogId, updateBlogRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @PutMapping("/{blogId}/background/mainColor")
    fun updateBackgroundMainColor(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @RequestBody updateBackgroundMainColorRequest: UpdateBackgroundMainColorRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.updateBlogMainColor(userId, blogId, updateBackgroundMainColorRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @PutMapping("/{blogId}/background/image")
    fun updateBackgroundImage(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
        @RequestBody updateBackgroundImageRequest: UpdateBackgroundImageRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.updateBlogBackgroundImage(userId, blogId, updateBackgroundImageRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @DeleteMapping("/{blogId}")
    fun delete(
        @AuthenticationPrincipal userId: Long,
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.deleteBlog(userId, blogId)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )
}
