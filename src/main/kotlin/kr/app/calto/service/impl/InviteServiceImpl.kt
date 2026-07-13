package kr.app.calto.service.impl

import jakarta.transaction.Transactional
import kr.app.calto.controller.dto.request.invite.JoinBlogRequest
import kr.app.calto.domain.MemberRole
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.infrastructure.entities.BlogMemberEntity
import kr.app.calto.infrastructure.entities.InviteEntity
import kr.app.calto.infrastructure.entities.UserEntity
import kr.app.calto.infrastructure.repository.BlogMemberRepository
import kr.app.calto.infrastructure.repository.BlogRepository
import kr.app.calto.infrastructure.repository.InviteRepository
import kr.app.calto.infrastructure.repository.UserRepository
import kr.app.calto.service.BlogAuthorizationService
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
    private val blogAuthorizationService: BlogAuthorizationService,
    @Value("\${app.frontend.invite-base-url}") private val inviteBaseUrl: String,
    @Value("\${app.invite.code-ttl-hours}") private val codeTtlHours: Long,
    @Value("\${app.blog.max-members}") private val maxMembers: Int,
    @Value("\${app.blog.max-blogs-per-user}") private val maxBlogsPerUser: Int,
) : InviteService {
    override fun createInviteCode(
        userId: Long,
        blogId: Long,
    ): InviteCreatedResult {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER, MemberRole.ADMIN)

        blogRepository
            .findById(blogId)
            .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }

        val currentMemberCount = blogMemberRepository.countByBlogIdAndDeletedAtIsNull(blogId)
        if (currentMemberCount >= maxMembers) {
            throw CalToException(
                ErrorCode.BLOG_MAX_MEMBERS_REACHED,
                "블로그 최대 멤버 수(${maxMembers}명) 도달로 초대 코드 생성 불가",
            )
        }

        // TODO: find 대신 exist 전환 검토 필요
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
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER, MemberRole.ADMIN)

        val activeInviteCode =
            inviteRepository.findActiveByCreator(
                blogId = blogId,
                userId = userId,
                now = LocalDateTime.now(),
            ) ?: return null

        return toInviteCreatedResult(blogId, activeInviteCode.code, activeInviteCode.expiresAt)
    }

    override fun deleteInviteCode(
        userId: Long,
        blogId: Long,
        code: String,
    ) {
        blogAuthorizationService.requireRole(blogId, userId, MemberRole.OWNER, MemberRole.ADMIN)

        val inviteCode = inviteRepository.findByBlogIdAndCode(blogId, code)

        if (inviteCode == null ||
            inviteCode.inviteUserId != userId ||
            inviteCode.usedUserId != null ||
            inviteCode.expiresAt.isBefore(LocalDateTime.now())
        ) {
            throw CalToException(ErrorCode.INVITE_NOT_FOUND)
        }

        inviteRepository.delete(inviteCode)
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
        joinBlogRequest: JoinBlogRequest,
    ) {
        val now = LocalDateTime.now()

        val inviteCode = checkInviteCode(blogId, code, now)
        val user = checkUser(userId, blogId)
        checkBlog(blogId)

        //  useUserProfile = true : OAuth 프로필(닉네임, 이미지) 그대로 사용 (name 무시)
        //  useUserProfile = false : 사용자가 name 직접 입력, imageUrl 은 default
        val (name, imageUrl) =
            if (joinBlogRequest.useUserProfile) {
                user.nickname to (user.profileImageUrl ?: "default-profile.jpg")
            } else {
                joinBlogRequest.name to "default-profile.jpg"
            }

        checkNickname(blogId, name)

        // CAS — invite 점유를 원자적으로 처리. 0행이면 다른 트랜잭션이 먼저 차지함
        val affected = inviteRepository.markUsedIfUnused(inviteCode.id, userId, now)
        if (affected == 0) {
            throw CalToException(ErrorCode.INVITE_CODE_USED)
        }

        blogMemberRepository.save(
            BlogMemberEntity(
                blogId = blogId,
                userId = userId,
                name = name,
                imageUrl = imageUrl,
                comments = null,
                role = MemberRole.MEMBER,
                updatedAt = null,
                deletedAt = null,
            ),
        )
    }

    private fun checkInviteCode(
        blogId: Long,
        code: String,
        now: LocalDateTime,
    ): InviteEntity {
        // 초대 코드 존재 검증
        val inviteCode =
            inviteRepository.findByBlogIdAndCode(blogId, code)
                ?: throw CalToException(ErrorCode.INVITE_CODE_INVALID)
        // 초대 코드 사용 여부 검증
        if (inviteCode.usedUserId != null) {
            throw CalToException(ErrorCode.INVITE_CODE_USED)
        }
        // 초대 코드 만료 검증
        if (inviteCode.expiresAt.isBefore(now)) {
            throw CalToException(ErrorCode.INVITE_CODE_EXPIRED)
        }
        return inviteCode
    }

    private fun checkUser(
        userId: Long,
        blogId: Long,
    ): UserEntity {
        // user 존재 검증
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { CalToException(ErrorCode.USER_NOT_FOUND) }

        // 이미 블로그 멤버에 속한 상태인지 검증
        if (blogMemberRepository.existsByBlogIdAndUserIdAndDeletedAtIsNull(blogId, userId)) {
            throw CalToException(ErrorCode.BLOG_ALREADY_JOINED)
        }
        // 인당 최대 블로그 수 검증
        val currentBlogCount = blogMemberRepository.countByUserIdAndDeletedAtIsNull(userId)
        if (currentBlogCount >= maxBlogsPerUser) {
            throw CalToException(
                ErrorCode.USER_MAX_BLOGS_REACHED,
                "최대 ${maxBlogsPerUser}개의 블로그까지 소속 가능합니다",
            )
        }
        return user
    }

    private fun checkBlog(blogId: Long) {
        // blog 존재 검증
        blogRepository
            .findById(blogId)
            .orElseThrow { CalToException(ErrorCode.BLOG_NOT_FOUND) }
        // 최대 멤버 수 검증
        val currentMemberCount = blogMemberRepository.countByBlogIdAndDeletedAtIsNull(blogId)
        if (currentMemberCount >= maxMembers) {
            throw CalToException(
                ErrorCode.BLOG_MAX_MEMBERS_REACHED,
                "블로그 최대 멤버 수(${maxMembers}명) 도달로 가입 불가",
            )
        }
    }

    private fun checkNickname(
        blogId: Long,
        name: String,
    ) {
        if (blogMemberRepository.existsByBlogIdAndNameAndDeletedAtIsNull(blogId, name)) {
            throw CalToException(ErrorCode.BLOG_MEMBER_NAME_DUPLICATED)
        }
    }
}
