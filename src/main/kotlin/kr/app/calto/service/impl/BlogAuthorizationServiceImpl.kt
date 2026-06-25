package kr.app.calto.service.impl

import kr.app.calto.domain.MemberRole
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.infrastructure.entities.BlogMemberEntity
import kr.app.calto.infrastructure.repository.BlogMemberRepository
import kr.app.calto.service.BlogAuthorizationService
import org.springframework.stereotype.Service

@Service
class BlogAuthorizationServiceImpl(
    private val blogMemberRepository: BlogMemberRepository,
) : BlogAuthorizationService {
    override fun requireMember(
        blogId: Long,
        userId: Long,
    ): BlogMemberEntity =
        blogMemberRepository.findByBlogIdAndUserId(blogId, userId)
            ?: throw CalToException(
                ErrorCode.BLOG_PERMISSION_DENIED,
                "해당 블로그 소속이 아닙니다",
            )

    override fun requireRole(
        blogId: Long,
        userId: Long,
        vararg allowedRoles: MemberRole,
    ): BlogMemberEntity {
        val member = requireMember(blogId, userId)
        if (member.role !in allowedRoles) {
            throw CalToException(
                ErrorCode.BLOG_PERMISSION_DENIED,
                "해당 권한이 없습니다",
            )
        }
        return member
    }
}
