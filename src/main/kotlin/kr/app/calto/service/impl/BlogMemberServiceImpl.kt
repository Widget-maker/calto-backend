package kr.app.calto.service.impl

import kr.app.calto.controller.dto.request.blogMember.UpdateMemberRoleRequest
import kr.app.calto.infrastructure.repository.BlogMemberRepository
import kr.app.calto.service.BlogMemberService
import kr.app.calto.service.dto.BlogMemberDetail
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BlogMemberServiceImpl(
    private val blogMemberRepository: BlogMemberRepository,
) : BlogMemberService {
    // TODO: 타겟 멤버 정보 확인인지, 본인 정보 확인인지 구분 필요
    override fun getBlogMember(
        blogId: Long,
        blogMemberId: Long,
    ): BlogMemberDetail {
        val entity =
            blogMemberRepository.findByBlogIdAndId(blogId, blogMemberId)
                ?: throw NoSuchElementException("블로그 멤버 조회 실패")

        return BlogMemberDetail.from(entity.toDomain())
    }

    override fun getBlogMembers(blogId: Long): List<BlogMemberDetail> {
        val members = blogMemberRepository.findByBlogId(blogId)
            .map { BlogMemberDetail.from(it.toDomain()) }

        return members
    }

    override fun updateMemberRole(
        blogId: Long,
        blogMemberId: Long,
        updatedMemberRoleRequest: UpdateMemberRoleRequest,
    ) {
        val entity =
            blogMemberRepository.findByBlogIdAndId(blogId, blogMemberId)
                ?: throw NoSuchElementException("블로그 멤버 조회 실패")

        entity.role = updatedMemberRoleRequest.role
        entity.updatedAt = LocalDateTime.now()

        blogMemberRepository.save(entity)
    }

    // 본인 스스로 삭제
    override fun leaveBlog(
        blogId: Long,
        blogMemberId: Long,
    ) {
        val entity =
            blogMemberRepository.findByBlogIdAndId(blogId, blogMemberId)
                ?: throw NoSuchElementException("블로그 멤버 조회 실패")

        entity.deletedAt = LocalDateTime.now()
        blogMemberRepository.save(entity)
    }

    // 다른 권한자에 의해 삭제. targetMemberId 삭제
    override fun deleteBlogMember(
        blogId: Long,
        targetMemberId: Long,
    ) {
        val entity =
            blogMemberRepository.findByBlogIdAndId(blogId, targetMemberId)
                ?: throw NoSuchElementException("블로그 멤버 조회 실패")

        entity.deletedAt = LocalDateTime.now()
        blogMemberRepository.save(entity)
    }
}
