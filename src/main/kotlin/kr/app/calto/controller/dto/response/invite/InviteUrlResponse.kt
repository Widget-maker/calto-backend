package kr.app.calto.controller.dto.response.invite

import kr.app.calto.service.dto.InviteCreatedResult

class InviteUrlResponse(
    val inviteUrl: String,
    val expiresAt: String,
) {
    constructor(inviteCreatedResult: InviteCreatedResult) : this(
        inviteUrl = inviteCreatedResult.inviteUrl,
        expiresAt = inviteCreatedResult.expiresAt,
    )
}
