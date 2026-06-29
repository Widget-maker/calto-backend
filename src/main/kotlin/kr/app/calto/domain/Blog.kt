package kr.app.calto.domain

import java.time.LocalDateTime

data class Blog(
    val id: Long,
    val name: String,
    val imageUrl: String,
    // TODO: 멤버 목록과 멤버별 상세 정보 List<BlogMember>
    val members: Int,
    val mainColor: BlogColor,
    val backgroundImageUrl: String?,
    val backgroundType: BackgroundType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
)
