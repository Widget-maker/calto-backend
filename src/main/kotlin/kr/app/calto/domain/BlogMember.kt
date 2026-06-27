package kr.app.calto.domain

import java.time.LocalDateTime

data class BlogMember(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val comments: String?,
    val role: MemberRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
)
