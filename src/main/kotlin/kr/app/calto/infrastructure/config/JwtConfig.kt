package kr.app.calto.infrastructure.config

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kr.app.calto.infrastructure.security.JwtProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
class JwtConfig {
    @Bean
    fun jwtSecretKey(jwtProperties: JwtProperties): SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    @Bean
    fun jwtParser(jwtSecretKey: SecretKey): JwtParser =
        Jwts.parser()
            .verifyWith(jwtSecretKey)
            .build()
}
