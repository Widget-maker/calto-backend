package kr.app.calto.service.impl

import kr.app.calto.controller.dto.request.blog.CreateBlogRequest
import kr.app.calto.controller.dto.request.blog.UpdateBackgroundImageRequest
import kr.app.calto.controller.dto.request.blog.UpdateBackgroundMainColorRequest
import kr.app.calto.controller.dto.request.blog.UpdateBlogRequest
import kr.app.calto.domain.BackgroundType
import kr.app.calto.domain.BlogColor
import kr.app.calto.domain.MemberRole
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.infrastructure.entities.BlogEntity
import kr.app.calto.infrastructure.repository.BlogRepository
import kr.app.calto.service.BlogAuthorizationService
import kr.app.calto.service.BlogService
import kr.app.calto.service.dto.BlogDetail
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BlogServiceImpl(
    private val blogRepository: BlogRepository,
    private val blogAuthorizationService: BlogAuthorizationService,
) : BlogService {
    override fun getAllBlogs(userId: Long): List<BlogDetail> =
        blogRepository
            .findAllByMemberUserId(userId)
            .map { BlogDetail.from(it.toDomain()) }

    // 비소속 사용자는 403 BLOG_PERMISSION_DENIED
    // 블로그가 delete된 경우는 404 BLOG_NOT_FOUND
    override fun getBlogById(
        userId: Long,
        blogId: Long,
    ): BlogDetail {
        blogAuthorizationService.requireMember(blogId, userId)

        val entity =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        if (entity.deletedAt != null) {
            throw CalToException(ErrorCode.BLOG_NOT_FOUND)
        }

        return BlogDetail.from(entity.toDomain())
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
        userId: Long,
        blogId: Long,
        updateBlogRequest: UpdateBlogRequest,
    ) {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER)

        val entity =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        updateBlogRequest.name?.let { entity.name = it }
        updateBlogRequest.imageUrl?.let { entity.imageUrl = it }
        entity.updatedAt = LocalDateTime.now()

        blogRepository.save(entity)
    }

    override fun updateBlogMainColor(
        userId: Long,
        blogId: Long,
        updateBackgroundMainColorRequest: UpdateBackgroundMainColorRequest,
    ) {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER, MemberRole.ADMIN)

        val entity =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        entity.mainColor = updateBackgroundMainColorRequest.mainColor
        entity.backgroundType = BackgroundType.COLOR
        entity.updatedAt = LocalDateTime.now()

        blogRepository.save(entity)
    }

    override fun updateBlogBackgroundImage(
        userId: Long,
        blogId: Long,
        updateBackgroundImageRequest: UpdateBackgroundImageRequest,
    ) {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER, MemberRole.ADMIN)

        val entity =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        entity.backgroundImageUrl = updateBackgroundImageRequest.backgroundImageUrl
        entity.backgroundType = BackgroundType.IMAGE
        entity.updatedAt = LocalDateTime.now()

        blogRepository.save(entity)
    }
}
