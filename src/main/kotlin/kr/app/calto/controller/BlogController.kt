package kr.app.calto.controller

import kr.app.calto.controller.dto.request.blog.CreateBlogRequest
import kr.app.calto.controller.dto.request.blog.UpdateBlogRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.blog.BlogResponse
import kr.app.calto.service.BlogService
import org.springframework.http.ResponseEntity
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
            onSuccess = { blogs -> ResponseEntity.ok(ApiResponse.success(blogs.map { BlogResponse(it) })) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @GetMapping("/{blogId}")
    fun getBlog(
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<BlogResponse>> =
        runCatching {
            blogService.getBlogById(blogId)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success(BlogResponse(it))) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @PostMapping
    fun create(
        @RequestBody createBlogRequest: CreateBlogRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.createBlog(createBlogRequest)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success()) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @PutMapping("/{blogId}")
    fun update(
        @PathVariable blogId: Long,
        @RequestBody updateBlogRequest: UpdateBlogRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.updateBlog(blogId, updateBlogRequest)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success()) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )

    @DeleteMapping("/{blogId}")
    fun delete(
        @PathVariable blogId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            blogService.deleteBlog(blogId)
        }.fold(
            onSuccess = { ResponseEntity.ok(ApiResponse.success()) },
            onFailure = { ResponseEntity.status(400).body(ApiResponse.failure(400, "error")) },
        )
}
