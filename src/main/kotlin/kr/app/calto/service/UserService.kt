package kr.app.calto.service

import kr.app.calto.controller.dto.request.user.CreateProfileRequest
import kr.app.calto.controller.dto.request.user.UpdateProfileRequest
import kr.app.calto.domain.User
import kr.app.calto.service.dto.OAuthUserInfo
import kr.app.calto.service.dto.UserUpsertResult

interface UserService {
    fun findOrInitFromOAuth(oAuthUserInfo: OAuthUserInfo): UserUpsertResult

    fun completeProfile(
        userId: Long,
        createProfileRequest: CreateProfileRequest,
    )

    fun isNicknameDuplicated(nickname: String): Boolean

    fun getMyProfile(userId: Long): User

    fun updateMyProfile(
        userId: Long,
        updateProfileRequest: UpdateProfileRequest,
    )

    fun withdraw(userId: Long)
}
