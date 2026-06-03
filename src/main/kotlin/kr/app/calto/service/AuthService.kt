package kr.app.calto.service

import kr.app.calto.controller.dto.request.auth.OAuthCallbackRequest
import kr.app.calto.controller.dto.request.auth.RefreshTokenRequest
import kr.app.calto.domain.AuthProvider
import kr.app.calto.service.dto.AuthTokenResult

interface AuthService {
    fun handleOAuthCallback(
        provider: AuthProvider,
        oAuthCallbackRequest: OAuthCallbackRequest,
    ): AuthTokenResult

    fun refresh(refreshTokenRequest: RefreshTokenRequest): AuthTokenResult

    fun logout(refreshTokenRequest: RefreshTokenRequest)
}
