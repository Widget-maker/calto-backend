package kr.app.calto.controller.dto.response.auth

import kr.app.calto.service.dto.AuthTokenResult

class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val isNewUser: Boolean,
) {
    constructor(authTokenResult: AuthTokenResult) : this(
        accessToken = authTokenResult.accessToken,
        refreshToken = authTokenResult.refreshToken,
        userId = authTokenResult.userId,
        isNewUser = authTokenResult.isNewUser,
    )
}
