package kr.app.calto.infrastructure.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
    private val key: SecretKey,
    private val jwtParser: JwtParser,
) {
    fun issueAccessToken(userId: Long): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpirationMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun issueRefreshToken(userId: Long): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.refreshTokenExpirationMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun parseUserId(token: String): Long =
        jwtParser
            .parseSignedClaims(token)
            .payload
            .subject
            .toLong()

    fun refreshTokenExpiresAt(): LocalDateTime =
        Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpirationMs)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
}
