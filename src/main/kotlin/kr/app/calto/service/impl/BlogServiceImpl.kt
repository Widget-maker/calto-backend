package kr.app.calto.service.impl

import jakarta.transaction.Transactional
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
import kr.app.calto.infrastructure.entities.BlogMemberEntity
import kr.app.calto.infrastructure.repository.BlogMemberRepository
import kr.app.calto.infrastructure.repository.BlogRepository
import kr.app.calto.infrastructure.repository.UserRepository
import kr.app.calto.service.BlogAuthorizationService
import kr.app.calto.service.BlogService
import kr.app.calto.service.dto.BlogDetail
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BlogServiceImpl(
    private val blogRepository: BlogRepository,
    private val blogMemberRepository: BlogMemberRepository,
    private val userRepository: UserRepository,
    private val blogAuthorizationService: BlogAuthorizationService,
    @Value("\${app.blog.max-blogs-per-user}") private val maxBlogsPerUser: Int,
) : BlogService {
    override fun getAllBlogs(userId: Long): List<BlogDetail> =
        blogRepository
            .findAllByMemberUserId(userId)
            .map { BlogDetail.from(it.toDomain()) }

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

    @Transactional
    override fun createBlog(
        userId: Long,
        createBlogRequest: CreateBlogRequest,
    ) {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { CalToException(ErrorCode.USER_NOT_FOUND) }

        val currentBlogCount = blogMemberRepository.countByUserIdAndDeletedAtIsNull(userId)
        if (currentBlogCount >= maxBlogsPerUser) {
            throw CalToException(
                ErrorCode.USER_MAX_BLOGS_REACHED,
                "최대 ${maxBlogsPerUser}개의 블로그까지 소속 가능합니다",
            )
        }

        val blog =
            blogRepository.save(
                BlogEntity(
                    name = createBlogRequest.name,
                    members = 1,
                    imageUrl = createBlogRequest.imageUrl ?: "default-image.jpg",
                    mainColor = BlogColor.WHITE,
                ),
            )

        blogMemberRepository.save(
            BlogMemberEntity(
                blogId = blog.id,
                userId = userId,
                name = user.nickname,
                imageUrl = user.profileImageUrl ?: "default-profile.jpg",
                role = MemberRole.OWNER,
                updatedAt = null,
                deletedAt = null,
            ),
        )
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
