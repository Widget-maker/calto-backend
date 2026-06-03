package kr.app.calto.domain

import java.time.LocalDateTime

data class User(
    val id: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val isProfileSet: Boolean,
    val provider: AuthProvider,
    val providerId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
)
