package kr.app.calto.controller

import kr.app.calto.controller.dto.request.auth.OAuthCallbackRequest
import kr.app.calto.controller.dto.request.auth.RefreshTokenRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.Responses
import kr.app.calto.controller.dto.response.auth.AuthTokenResponse
import kr.app.calto.domain.AuthProvider
import kr.app.calto.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/oauth2/{provider}/callback")
    fun oAuthCallback(
        @PathVariable provider: AuthProvider,
        @RequestBody oAuthCallbackRequest: OAuthCallbackRequest,
    ): ResponseEntity<ApiResponse<AuthTokenResponse>> =
        runCatching {
            authService.handleOAuthCallback(provider, oAuthCallbackRequest)
        }.fold(
            onSuccess = { Responses.success(AuthTokenResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody refreshTokenRequest: RefreshTokenRequest,
    ): ResponseEntity<ApiResponse<AuthTokenResponse>> =
        runCatching {
            authService.refresh(refreshTokenRequest)
        }.fold(
            onSuccess = { Responses.success(AuthTokenResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @PostMapping("/logout")
    fun logout(
        @RequestBody refreshTokenRequest: RefreshTokenRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            authService.logout(refreshTokenRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )
}
