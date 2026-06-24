package kr.app.calto.controller

import kr.app.calto.controller.dto.request.user.CreateProfileRequest
import kr.app.calto.controller.dto.request.user.UpdateProfileRequest
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.controller.dto.response.Responses
import kr.app.calto.controller.dto.response.user.NicknameCheckResponse
import kr.app.calto.controller.dto.response.user.UserProfileResponse
import kr.app.calto.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    // OAuth 인증 직후 최초 프로필 마무리 (닉네임/프로필 이미지 설정 + isProfileSet = true 전환)
    @PostMapping("/profile")
    fun completeProfile(
        @AuthenticationPrincipal userId: Long,
        @RequestBody createProfileRequest: CreateProfileRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            userService.completeProfile(userId, createProfileRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @GetMapping("/nickname/check")
    fun checkNickname(
        @RequestParam nickname: String,
    ): ResponseEntity<ApiResponse<NicknameCheckResponse>> =
        runCatching {
            userService.isNicknameDuplicated(nickname)
        }.fold(
            onSuccess = { Responses.success(NicknameCheckResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @GetMapping("/me")
    fun getMyProfile(
        @AuthenticationPrincipal userId: Long,
    ): ResponseEntity<ApiResponse<UserProfileResponse>> =
        runCatching {
            userService.getMyProfile(userId)
        }.fold(
            onSuccess = { Responses.success(UserProfileResponse(it)) },
            onFailure = { Responses.failure(it) },
        )

    @PutMapping("/me")
    fun updateMyProfile(
        @AuthenticationPrincipal userId: Long,
        @RequestBody updateProfileRequest: UpdateProfileRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            userService.updateMyProfile(userId, updateProfileRequest)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )

    @DeleteMapping("/me")
    fun withdraw(
        @AuthenticationPrincipal userId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> =
        runCatching {
            userService.withdraw(userId)
        }.fold(
            onSuccess = { Responses.success() },
            onFailure = { Responses.failure(it) },
        )
}
