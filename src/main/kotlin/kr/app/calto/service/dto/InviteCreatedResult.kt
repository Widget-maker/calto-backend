package kr.app.calto.service.dto

import java.time.LocalDateTime

class InviteCreatedResult(
    val inviteUrl: String,
    val expiresAt: LocalDateTime,
)
