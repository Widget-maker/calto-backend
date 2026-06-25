package kr.app.calto.controller.dto.response.blog

import kr.app.calto.domain.BackgroundType
import kr.app.calto.domain.BlogColor
import kr.app.calto.service.dto.BlogDetail

class BlogResponse(
    val name: String,
    val imageUrl: String?,
    val members: Int,
    val mainColor: BlogColor,
    val backgroundImageUrl: String?,
    val backgroundType: BackgroundType,
    val createdAt: String,
) {
    constructor(blogDetail: BlogDetail) : this(
        name = blogDetail.name,
        imageUrl = blogDetail.imageUrl,
        members = blogDetail.members,
        mainColor = blogDetail.mainColor,
        backgroundImageUrl = blogDetail.backgroundImageUrl,
        backgroundType = blogDetail.backgroundType,
        createdAt = blogDetail.createdAt,
    )
}
