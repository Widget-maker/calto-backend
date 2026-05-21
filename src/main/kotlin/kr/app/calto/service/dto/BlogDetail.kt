package kr.app.calto.service.dto

import kr.app.calto.domain.Blog
import java.time.format.DateTimeFormatter

class BlogDetail(
    val name: String,
    val imageUrl: String?,
    val members: Int,
    val createdAt: String,
) {
    companion object {
        fun from(blog: Blog): BlogDetail =
            BlogDetail(
                name = blog.name,
                imageUrl = blog.imageUrl,
                members = blog.members,
                createdAt = blog.createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")),
            )
    }
}
