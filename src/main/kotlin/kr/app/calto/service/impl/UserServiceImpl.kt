package kr.app.calto.service.impl

import kr.app.calto.controller.dto.request.user.CreateProfileRequest
import kr.app.calto.controller.dto.request.user.UpdateProfileRequest
import kr.app.calto.domain.User
import kr.app.calto.infrastructure.entities.UserEntity
import kr.app.calto.infrastructure.repository.UserRepository
import kr.app.calto.service.UserService
import kr.app.calto.service.dto.OAuthUserInfo
import kr.app.calto.service.dto.UserUpsertResult
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun findOrInitFromOAuth(oAuthUserInfo: OAuthUserInfo): UserUpsertResult {
        val existing =
            userRepository.findByProviderAndProviderId(
                provider = oAuthUserInfo.provider,
                providerId = oAuthUserInfo.providerId,
            )

        if (existing != null) {
            return UserUpsertResult(user = existing.toDomain(), isNewUser = false)
        }

        val created =
            userRepository.save(
                UserEntity(
                    nickname = oAuthUserInfo.nickname,
                    profileImageUrl = oAuthUserInfo.profileImageUrl,
                    isProfileSet = false,
                    provider = oAuthUserInfo.provider,
                    providerId = oAuthUserInfo.providerId,
                ),
            )

        return UserUpsertResult(user = created.toDomain(), isNewUser = true)
    }

    override fun completeProfile(
        userId: Long,
        createProfileRequest: CreateProfileRequest,
    ) {
        val entity =
            userRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저 조회 실패") }

        entity.nickname = createProfileRequest.nickname
        createProfileRequest.profileImageUrl?.let { entity.profileImageUrl = it }
        entity.isProfileSet = true
        entity.updatedAt = LocalDateTime.now()

        userRepository.save(entity)
    }

    override fun isNicknameDuplicated(nickname: String): Boolean = userRepository.existsByNickname(nickname)

    override fun getMyProfile(userId: Long): User {
        val user =
            userRepository
                .findById(userId)
                .map { it.toDomain() }
                .orElseThrow { NoSuchElementException("유저 조회 실패") }

        return user
    }

    override fun updateMyProfile(
        userId: Long,
        updateProfileRequest: UpdateProfileRequest,
    ) {
        val entity =
            userRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저 조회 실패") }

        updateProfileRequest.nickname?.let { entity.nickname = it }
        updateProfileRequest.profileImageUrl?.let { entity.profileImageUrl = it }
        entity.updatedAt = LocalDateTime.now()

        userRepository.save(entity)
    }

    // TODO: deletedAt 필터링 필요
    override fun withdraw(userId: Long) {
        val entity =
            userRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저 조회 실패") }

        entity.deletedAt = LocalDateTime.now()
        userRepository.save(entity)
    }
}
