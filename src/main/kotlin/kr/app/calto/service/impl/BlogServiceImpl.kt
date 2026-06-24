package kr.app.calto.service.impl

import kr.app.calto.controller.dto.request.blog.CreateBlogRequest
import kr.app.calto.controller.dto.request.blog.UpdateBlogRequest
import kr.app.calto.domain.BlogColor
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.infrastructure.entities.BlogEntity
import kr.app.calto.infrastructure.repository.BlogRepository
import kr.app.calto.service.BlogService
import kr.app.calto.service.dto.BlogDetail
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BlogServiceImpl(
    private val blogRepository: BlogRepository,
) : BlogService {
    override fun getAllBlogs(): List<BlogDetail> {
        val blogs =
            blogRepository
                .findAll()
                .map { BlogDetail.from(it.toDomain()) }

        return blogs
    }

    override fun getBlogById(id: Long): BlogDetail {
        val blog =
            blogRepository
                .findById(id)
                .map { BlogDetail.from(it.toDomain()) }
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        return blog
    }

    override fun createBlog(createBlogRequest: CreateBlogRequest) {
        val entity =
            BlogEntity(
                name = createBlogRequest.name,
                members = 1,
                imageUrl = createBlogRequest.imageUrl ?: "default-image.jpg",
                mainColor = BlogColor.WHITE,
            )
        blogRepository.save(entity)
    }

    override fun updateBlog(
        blogId: Long,
        updateBlogRequest: UpdateBlogRequest,
    ) {
        val entity =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        updateBlogRequest.name?.let { entity.name = it }
        updateBlogRequest.imageUrl?.let { entity.imageUrl = it }
        updateBlogRequest.mainColor?.let { entity.mainColor = it }
        entity.updatedAt = LocalDateTime.now()

        blogRepository.save(entity)
    }

    override fun deleteBlog(blogId: Long) {
        val entity =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        entity.deletedAt = LocalDateTime.now()
        blogRepository.save(entity)
    }
}
