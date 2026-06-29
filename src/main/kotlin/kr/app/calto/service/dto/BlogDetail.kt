package kr.app.calto.service.dto

import kr.app.calto.domain.BackgroundType
import kr.app.calto.domain.Blog
import kr.app.calto.domain.BlogColor
import java.time.format.DateTimeFormatter

class BlogDetail(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val members: Int,
    val mainColor: BlogColor,
    val backgroundImageUrl: String?,
    val backgroundType: BackgroundType,
    val createdAt: String,
) {
    constructor(blog: Blog, memberCount: Int) : this(
        id = blog.id,
        name = blog.name,
        imageUrl = blog.imageUrl,
        members = memberCount,
        mainColor = blog.mainColor,
        backgroundImageUrl = blog.backgroundImageUrl,
        backgroundType = blog.backgroundType,
        createdAt = blog.createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")),
    )
}
