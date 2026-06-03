package kr.app.calto.infrastructure.repository

import kr.app.calto.domain.AuthProvider
import kr.app.calto.infrastructure.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByProviderAndProviderId(
        provider: AuthProvider,
        providerId: String,
    ): UserEntity?

    fun existsByNickname(nickname: String): Boolean
}
