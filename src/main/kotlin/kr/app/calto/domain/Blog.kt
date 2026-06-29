package kr.app.calto.domain

import java.time.LocalDateTime

data class Blog(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val mainColor: BlogColor,
    val backgroundImageUrl: String?,
    val backgroundType: BackgroundType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
)
