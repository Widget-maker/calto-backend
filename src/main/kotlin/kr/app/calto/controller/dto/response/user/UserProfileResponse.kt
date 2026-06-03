package kr.app.calto.controller.dto.response.user

import kr.app.calto.domain.AuthProvider
import kr.app.calto.domain.User

class UserProfileResponse(
    val nickname: String,
    val profileImageUrl: String?,
    val provider: AuthProvider,
    val createdAt: String,
) {
    constructor(user: User) : this(
        nickname = user.nickname,
        profileImageUrl = user.profileImageUrl,
        provider = user.provider,
        createdAt = user.createdAt.toString(),
    )
}
