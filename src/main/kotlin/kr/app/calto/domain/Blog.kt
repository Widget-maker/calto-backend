package kr.app.calto.domain

import java.time.LocalDateTime

data class Blog(
    val name: String,
    val imageUrl: String,
    // TODO: 멤버 목록과 멤버별 상세 정보 List<BlogMember>
    val members: Int,
    // TODO: 배경화면 이미지 사용시 image url 필드 필요,
    //       제공되는 디폴트 컬러 사용유무 boolean 판단 필드 필요
    val mainColor: BlogColor,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
)
