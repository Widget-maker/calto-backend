package kr.app.calto.service

import kr.app.calto.controller.dto.request.blog.CreateBlogRequest
import kr.app.calto.controller.dto.request.blog.UpdateBackgroundImageRequest
import kr.app.calto.controller.dto.request.blog.UpdateBackgroundMainColorRequest
import kr.app.calto.controller.dto.request.blog.UpdateBlogRequest
import kr.app.calto.service.dto.BlogDetail

interface BlogService {
    fun getAllBlogs(): List<BlogDetail>

    fun getBlogById(id: Long): BlogDetail

    fun createBlog(createBlogRequest: CreateBlogRequest)

    fun updateBlog(
        userId: Long,
        blogId: Long,
        updateBlogRequest: UpdateBlogRequest,
    )

    fun updateBlogMainColor(
        userId: Long,
        blogId: Long,
        updateBackgroundMainColorRequest: UpdateBackgroundMainColorRequest,
    )

    fun updateBlogBackgroundImage(
        userId: Long,
        blogId: Long,
        updateBackgroundImageRequest: UpdateBackgroundImageRequest,
    )
}
