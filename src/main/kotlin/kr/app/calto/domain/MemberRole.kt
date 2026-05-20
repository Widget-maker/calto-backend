package kr.app.calto.domain

enum class MemberRole(
    val role: String
) {
    OWNER("owner"),
    ADMIN("admin"),
    MEMBER("member"),
}