package kr.app.calto.service.impl

import jakarta.transaction.Transactional
import kr.app.calto.controller.dto.request.auth.OAuthCallbackRequest
import kr.app.calto.controller.dto.request.auth.RefreshTokenRequest
import kr.app.calto.domain.AuthProvider
import kr.app.calto.infrastructure.entities.RefreshTokenEntity
import kr.app.calto.infrastructure.oauth.OAuthUserInfoClient
import kr.app.calto.infrastructure.repository.RefreshTokenRepository
import kr.app.calto.infrastructure.security.JwtProvider
import kr.app.calto.service.AuthService
import kr.app.calto.service.UserService
import kr.app.calto.service.dto.AuthTokenResult
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class AuthServiceImpl(
    private val userService: UserService,
    private val oAuthUserInfoClient: OAuthUserInfoClient,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
) : AuthService {
    override fun handleOAuthCallback(
        provider: AuthProvider,
        oAuthCallbackRequest: OAuthCallbackRequest,
    ): AuthTokenResult {
        val oAuthUserInfo = oAuthUserInfoClient.fetch(provider, oAuthCallbackRequest.code)
        val upserted = userService.findOrInitFromOAuth(oAuthUserInfo)
        return issueTokens(userId = upserted.user.id, isNewUser = upserted.isNewUser)
    }

    override fun refresh(refreshTokenRequest: RefreshTokenRequest): AuthTokenResult {
        val stored =
            refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken)
                ?: throw IllegalArgumentException("유효하지 않은 refresh token 입니다.")

        if (stored.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored)
            throw IllegalArgumentException("만료된 refresh token 입니다.")
        }

        val userId = jwtProvider.parseUserId(refreshTokenRequest.refreshToken)
        refreshTokenRepository.delete(stored)
        return issueTokens(userId = userId, isNewUser = false)
    }

    override fun logout(refreshTokenRequest: RefreshTokenRequest) {
        refreshTokenRepository.deleteByToken(refreshTokenRequest.refreshToken)
    }

    private fun issueTokens(
        userId: Long,
        isNewUser: Boolean,
    ): AuthTokenResult {
        val accessToken = jwtProvider.issueAccessToken(userId)
        val refreshToken = jwtProvider.issueRefreshToken(userId)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                token = refreshToken,
                expiresAt = jwtProvider.refreshTokenExpiresAt(),
            ),
        )

        return AuthTokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            isNewUser = isNewUser,
        )
    }
}
