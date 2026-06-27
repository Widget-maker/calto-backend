package kr.app.calto.controller.dto.response.blog

import kr.app.calto.service.dto.BlogDetail

class BlogSummaryResponse(
    val name: String,
    val imageUrl: String?,
    val members: Int,
    val createdAt: String,
) {
    constructor(blogDetail: BlogDetail) : this(
        name = blogDetail.name,
        imageUrl = blogDetail.imageUrl,
        members = blogDetail.members,
        createdAt = blogDetail.createdAt,
    )
}
