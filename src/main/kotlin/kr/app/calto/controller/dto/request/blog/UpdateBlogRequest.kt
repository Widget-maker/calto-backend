package kr.app.calto.controller.dto.request.blog

import kr.app.calto.domain.BlogColor

class UpdateBlogRequest(
    val name: String?,
    val imageUrl: String?,
    val mainColor: BlogColor?,
)
