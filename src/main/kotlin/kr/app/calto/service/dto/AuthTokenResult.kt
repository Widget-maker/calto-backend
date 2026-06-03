package kr.app.calto.service.dto

class AuthTokenResult(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val isNewUser: Boolean,
)
