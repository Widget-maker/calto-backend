package kr.app.calto.exception

enum class ErrorCode(
    val status: Int,
    val message: String,
) {
    // 공통
    INVALID_REQUEST(400, "잘못된 요청"),
    UNAUTHORIZED(401, "인증이 필요합니다"),
    FORBIDDEN(403, "권한이 없습니다"),
    NOT_FOUND(404, "리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(405, "허용되지 않은 HTTP 메서드입니다"),
    CONFLICT(409, "요청이 현재 리소스 상태와 충돌합니다"),
    GONE(410, "리소스가 만료되었습니다"),
    UNSUPPORTED_MEDIA_TYPE(415, "지원하지 않는 미디어 타입입니다"),
    INTERNAL_ERROR(500, "서버 내부 오류가 발생했습니다"),

    // Auth
    REFRESH_TOKEN_INVALID(401, "유효하지 않은 refresh token 입니다"),
    REFRESH_TOKEN_EXPIRED(401, "만료된 refresh token 입니다"),

    // OAuth
    OAUTH_PROVIDER_NOT_REGISTERED(400, "등록되지 않은 OAuth 제공자"),
    OAUTH_AUTH_FAILED(400, "OAuth 인증에 실패했습니다"),

    // User
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다"),

    // Blog
    BLOG_NOT_FOUND(404, "블로그를 찾을 수 없습니다"),
    BLOG_MAX_MEMBERS_REACHED(409, "블로그 최대 멤버 수에 도달했습니다"),
    BLOG_ALREADY_JOINED(409, "이미 가입된 블로그입니다"),

    // BlogMember
    BLOG_MEMBER_NOT_FOUND(404, "블로그 멤버를 찾을 수 없습니다"),

    // Invite
    INVITE_NOT_FOUND(404, "활성 초대 코드를 찾을 수 없습니다"),
    INVITE_PERMISSION_DENIED(403, "초대 코드 생성/조회 권한이 없습니다"),
    INVITE_ALREADY_EXISTS(409, "이미 활성화된 초대 코드가 존재합니다"),
    INVITE_CODE_INVALID(400, "유효하지 않은 초대 코드"),
    INVITE_CODE_USED(409, "이미 사용된 초대 코드"),
    INVITE_CODE_EXPIRED(410, "만료된 초대 코드"),
}
