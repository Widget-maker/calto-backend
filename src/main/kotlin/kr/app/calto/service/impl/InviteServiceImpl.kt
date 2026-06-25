package kr.app.calto.service.impl

import jakarta.transaction.Transactional
import kr.app.calto.domain.MemberRole
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.infrastructure.entities.BlogMemberEntity
import kr.app.calto.infrastructure.entities.InviteEntity
import kr.app.calto.infrastructure.repository.BlogMemberRepository
import kr.app.calto.infrastructure.repository.BlogRepository
import kr.app.calto.infrastructure.repository.InviteRepository
import kr.app.calto.infrastructure.repository.UserRepository
import kr.app.calto.service.InviteService
import kr.app.calto.service.dto.InviteCreatedResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class InviteServiceImpl(
    private val inviteRepository: InviteRepository,
    private val blogRepository: BlogRepository,
    private val blogMemberRepository: BlogMemberRepository,
    private val userRepository: UserRepository,
    @Value("\${app.frontend.invite-base-url}") private val inviteBaseUrl: String,
    @Value("\${app.invite.code-ttl-hours}") private val codeTtlHours: Long,
    @Value("\${app.blog.max-members}") private val maxMembers: Int,
) : InviteService {
    override fun createInviteCode(
        userId: Long,
        blogId: Long,
    ): InviteCreatedResult {
        val caller =
            blogMemberRepository.findByBlogIdAndUserId(blogId, userId)
                ?: throw CalToException(ErrorCode.BLOG_MEMBER_NOT_FOUND)

        if (caller.role != MemberRole.OWNER && caller.role != MemberRole.ADMIN) {
            throw CalToException(ErrorCode.INVITE_PERMISSION_DENIED, "초대 코드 생성 권한 없음")
        }

        val blog =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        if (blog.members >= maxMembers) {
            throw CalToException(
                ErrorCode.BLOG_MAX_MEMBERS_REACHED,
                "블로그 최대 멤버 수(${maxMembers}명) 도달로 초대 코드 생성 불가",
            )
        }

        val existing =
            inviteRepository.findActiveByCreator(
                blogId = blogId,
                userId = userId,
                now = LocalDateTime.now(),
            )
        if (existing != null) {
            throw CalToException(ErrorCode.INVITE_ALREADY_EXISTS)
        }

        val code = UUID.randomUUID().toString().replace("-", "")
        val expiresAt = LocalDateTime.now().plusHours(codeTtlHours)

        inviteRepository.save(
            InviteEntity(
                blogId = blogId,
                code = code,
                inviteUserId = userId,
                expiresAt = expiresAt,
            ),
        )

        return toInviteCreatedResult(blogId, code, expiresAt)
    }

    override fun getActiveInviteCode(
        userId: Long,
        blogId: Long,
    ): InviteCreatedResult? {
        val caller =
            blogMemberRepository.findByBlogIdAndUserId(blogId, userId)
                ?: throw CalToException(ErrorCode.BLOG_MEMBER_NOT_FOUND)

        if (caller.role != MemberRole.OWNER && caller.role != MemberRole.ADMIN) {
            throw CalToException(ErrorCode.INVITE_PERMISSION_DENIED, "초대 코드 조회 권한 없음")
        }

        val active =
            inviteRepository.findActiveByCreator(
                blogId = blogId,
                userId = userId,
                now = LocalDateTime.now(),
            ) ?: return null

        return toInviteCreatedResult(blogId, active.code, active.expiresAt)
    }

    override fun deleteInviteCode(
        userId: Long,
        blogId: Long,
        code: String,
    ) {
        val invite = inviteRepository.findByBlogIdAndCode(blogId, code)

        if (invite == null ||
            invite.inviteUserId != userId ||
            invite.usedUserId != null ||
            invite.expiresAt.isBefore(LocalDateTime.now())
        ) {
            throw CalToException(ErrorCode.INVITE_NOT_FOUND)
        }

        inviteRepository.delete(invite)
    }

    private fun toInviteCreatedResult(
        blogId: Long,
        code: String,
        expiresAt: LocalDateTime,
    ): InviteCreatedResult =
        InviteCreatedResult(
            inviteUrl = "$inviteBaseUrl/blogs/$blogId/invite/$code",
            expiresAt = expiresAt.toString(),
        )

    override fun joinBlog(
        userId: Long,
        blogId: Long,
        code: String,
    ) {
        val now = LocalDateTime.now()

        // 1) 빠른 실패용 사전 검증 (동시성 보장은 아래 CAS 단계에서 수행)
        val invite =
            inviteRepository.findByBlogIdAndCode(blogId, code)
                ?: throw CalToException(ErrorCode.INVITE_CODE_INVALID)

        if (invite.usedUserId != null) {
            throw CalToException(ErrorCode.INVITE_CODE_USED)
        }
        if (invite.expiresAt.isBefore(now)) {
            throw CalToException(ErrorCode.INVITE_CODE_EXPIRED)
        }
        if (blogMemberRepository.existsByBlogIdAndUserId(blogId, userId)) {
            throw CalToException(ErrorCode.BLOG_ALREADY_JOINED)
        }

        val user =
            userRepository
                .findById(userId)
                .orElseThrow { CalToException(ErrorCode.USER_NOT_FOUND) }

        val blog =
            blogRepository
                .findById(blogId)
                .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        if (blog.members >= maxMembers) {
            throw CalToException(
                ErrorCode.BLOG_MAX_MEMBERS_REACHED,
                "블로그 최대 멤버 수(${maxMembers}명) 도달로 가입 불가",
            )
        }

        // 2) CAS — invite 점유를 원자적으로 처리. 0행이면 다른 트랜잭션이 먼저 차지함
        val affected = inviteRepository.markUsedIfUnused(invite.id, userId, now)
        if (affected == 0) {
            throw CalToException(ErrorCode.INVITE_CODE_USED)
        }

        // 3) 점유 성공 → BlogMember 생성 + 블로그 멤버 수 갱신
        blogMemberRepository.save(
            BlogMemberEntity(
                blogId = blogId,
                userId = userId,
                name = user.nickname,
                imageUrl = user.profileImageUrl ?: "default-profile.jpg",
                role = MemberRole.MEMBER,
                updatedAt = null,
                deletedAt = null,
            ),
        )

        blog.members += 1
        blog.updatedAt = now
        blogRepository.save(blog)
    }
}
