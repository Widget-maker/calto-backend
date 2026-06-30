package kr.app.calto.service.impl

import kr.app.calto.controller.dto.request.blogMember.UpdateMemberRoleRequest
import kr.app.calto.controller.dto.request.blogMember.UpdateMyMemberProfileRequest
import kr.app.calto.domain.MemberRole
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.infrastructure.repository.BlogMemberRepository
import kr.app.calto.service.BlogAuthorizationService
import kr.app.calto.service.BlogMemberService
import kr.app.calto.service.dto.BlogMemberDetail
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BlogMemberServiceImpl(
    private val blogMemberRepository: BlogMemberRepository,
    private val blogAuthorizationService: BlogAuthorizationService,
) : BlogMemberService {
    override fun getBlogMember(
        userId: Long,
        blogId: Long,
        blogMemberId: Long,
    ): BlogMemberDetail {
        blogAuthorizationService.requireMember(blogId, userId)

        val entity =
            blogMemberRepository.findByBlogIdAndIdAndDeletedAtIsNull(blogId, blogMemberId)
                ?: throw CalToException(ErrorCode.BLOG_MEMBER_NOT_FOUND)

        return BlogMemberDetail(entity.toDomain())
    }

    override fun getBlogMembers(
        userId: Long,
        blogId: Long,
    ): List<BlogMemberDetail> {
        blogAuthorizationService.requireMember(blogId, userId)

        val members =
            blogMemberRepository
                .findByBlogIdAndDeletedAtIsNull(blogId)
                .map { BlogMemberDetail(it.toDomain()) }

        return members
    }

    override fun getMyMemberProfile(
        userId: Long,
        blogId: Long,
    ): BlogMemberDetail =
        BlogMemberDetail(
            blogAuthorizationService.requireMember(blogId, userId).toDomain(),
        )

    override fun updateMyMemberProfile(
        userId: Long,
        blogId: Long,
        updateMyMemberProfileRequest: UpdateMyMemberProfileRequest,
    ) {
        val blogMember = blogAuthorizationService.requireMember(blogId, userId)

        updateMyMemberProfileRequest.name?.let { blogMember.name = it }
        updateMyMemberProfileRequest.imageUrl?.let { blogMember.imageUrl = it }
        updateMyMemberProfileRequest.comments?.let { blogMember.comments = it }
        blogMember.updatedAt = LocalDateTime.now()

        blogMemberRepository.save(blogMember)
    }

    override fun updateMemberRole(
        userId: Long,
        blogId: Long,
        blogMemberId: Long,
        updatedMemberRoleRequest: UpdateMemberRoleRequest,
    ) {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER)

        val entity =
            blogMemberRepository.findByBlogIdAndIdAndDeletedAtIsNull(blogId, blogMemberId)
                ?: throw CalToException(ErrorCode.BLOG_MEMBER_NOT_FOUND)

        if (entity.role == MemberRole.OWNER) {
            throw CalToException(
                ErrorCode.BLOG_PERMISSION_DENIED,
                "OWNER 권한은 변경할 수 없습니다",
            )
        }
        if (updatedMemberRoleRequest.role == MemberRole.OWNER) {
            throw CalToException(
                ErrorCode.BLOG_PERMISSION_DENIED,
                "OWNER 권한으로 변경할 수 없습니다",
            )
        }

        entity.role = updatedMemberRoleRequest.role
        entity.updatedAt = LocalDateTime.now()

        blogMemberRepository.save(entity)
    }

    override fun leaveBlog(
        userId: Long,
        blogId: Long,
    ) {
        val caller = blogAuthorizationService.requireMember(blogId, userId)

        caller.deletedAt = LocalDateTime.now()
        blogMemberRepository.save(caller)
    }

    override fun deleteBlogMember(
        userId: Long,
        blogId: Long,
        blogMemberId: Long,
    ) {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER)

        val entity =
            blogMemberRepository.findByBlogIdAndIdAndDeletedAtIsNull(blogId, blogMemberId)
                ?: throw CalToException(ErrorCode.BLOG_MEMBER_NOT_FOUND)

        entity.deletedAt = LocalDateTime.now()
        blogMemberRepository.save(entity)
    }
}
