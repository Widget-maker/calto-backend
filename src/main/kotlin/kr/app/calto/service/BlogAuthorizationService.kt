package kr.app.calto.service

import kr.app.calto.domain.MemberRole
import kr.app.calto.infrastructure.entities.BlogMemberEntity

interface BlogAuthorizationService {
    fun requireMember(
        blogId: Long,
        userId: Long,
    ): BlogMemberEntity

    fun requireRole(
        blogId: Long,
        userId: Long,
        vararg allowedRoles: MemberRole,
    ): BlogMemberEntity
}
