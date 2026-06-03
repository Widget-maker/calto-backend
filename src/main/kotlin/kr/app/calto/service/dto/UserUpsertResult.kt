package kr.app.calto.service.dto

import kr.app.calto.domain.User

class UserUpsertResult(
    val user: User,
    val isNewUser: Boolean,
)
