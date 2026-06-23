package kr.app.calto.service.impl

import jakarta.transaction.Transactional
import kr.app.calto.domain.MemberRole
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
                ?: throw IllegalStateException("블로그 멤버 조회 실패")

        if (caller.role != MemberRole.OWNER && caller.role != MemberRole.ADMIN) {
            throw IllegalStateException("초대 코드 생성 권한 없음")
        }

        val blog =
            blogRepository
                .findById(blogId)
                .orElseThrow { NoSuchElementException("블로그 조회 실패") }

        if (blog.members >= maxMembers) {
            throw IllegalStateException("블로그 최대 멤버 수(${maxMembers}명) 도달로 초대 코드 생성 불가")
        }

        val existing =
            inviteRepository.findActiveByCreator(
                blogId = blogId,
                userId = userId,
                now = LocalDateTime.now(),
            )
        if (existing != null) {
            throw IllegalStateException("이미 활성화된 초대 코드가 존재합니다. 만료 또는 사용 후 새로 생성할 수 있습니다.")
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
                ?: throw IllegalStateException("블로그 멤버 조회 실패")

        if (caller.role != MemberRole.OWNER && caller.role != MemberRole.ADMIN) {
            throw IllegalStateException("초대 코드 조회 권한 없음")
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
            throw NoSuchElementException("활성 초대 코드 조회 실패")
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
            expiresAt = expiresAt,
        )

    override fun joinBlog(
        userId: Long,
        blogId: Long,
        code: String,
    ) {
        val invite =
            inviteRepository.findByBlogIdAndCode(blogId, code)
                ?: throw NoSuchElementException("유효하지 않은 초대 코드")

        if (invite.usedUserId != null) {
            throw IllegalStateException("이미 사용된 초대 코드")
        }
        if (invite.expiresAt.isBefore(LocalDateTime.now())) {
            throw IllegalStateException("만료된 초대 코드")
        }
        if (blogMemberRepository.existsByBlogIdAndUserId(blogId, userId)) {
            throw IllegalStateException("이미 가입된 블로그")
        }

        val user =
            userRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저 조회 실패") }

        val blog =
            blogRepository
                .findById(blogId)
                .orElseThrow { NoSuchElementException("블로그 조회 실패") }

        if (blog.members >= maxMembers) {
            throw IllegalStateException("블로그 최대 멤버 수(${maxMembers}명) 도달로 가입 불가")
        }

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

        invite.usedUserId = userId
        invite.usedAt = LocalDateTime.now()
        inviteRepository.save(invite)

        blog.members += 1
        blog.updatedAt = LocalDateTime.now()
        blogRepository.save(blog)
    }
}
