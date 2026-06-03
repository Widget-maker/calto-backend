package kr.app.calto.service.dto

import kr.app.calto.domain.AuthProvider

class OAuthUserInfo(
    val provider: AuthProvider,
    val providerId: String,
    val nickname: String,
    val profileImageUrl: String?,
)
