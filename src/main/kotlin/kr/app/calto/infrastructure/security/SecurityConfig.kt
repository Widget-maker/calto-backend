package kr.app.calto.infrastructure.security

import jakarta.servlet.http.HttpServletResponse
import kr.app.calto.controller.dto.response.ApiResponse
import kr.app.calto.exception.ErrorCode
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import tools.jackson.databind.ObjectMapper

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig(
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .oauth2Login { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/auth/**",
                        "/health",
                        "/actuator/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }
            // 시큐리티 거절도 ApiResponse JSON 으로 통일
            //  - 인증 누락/잘못된 토큰 → 401 UNAUTHORIZED
            //  - 인증 성공했지만 권한 부족 → 403 FORBIDDEN
            .exceptionHandling { eh ->
                eh.authenticationEntryPoint { _, response, _ ->
                    writeJsonError(response, ErrorCode.UNAUTHORIZED)
                }
                eh.accessDeniedHandler { _, response, _ ->
                    writeJsonError(response, ErrorCode.FORBIDDEN)
                }
            }.addFilterBefore(
                JwtAuthenticationFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter::class.java,
            )
        return http.build()
    }

    private fun writeJsonError(
        response: HttpServletResponse,
        errorCode: ErrorCode,
    ) {
        response.status = errorCode.status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        val body =
            ApiResponse.failure<Nothing>(
                code = errorCode.status,
                errorCode = errorCode.name,
                message = errorCode.message,
            )
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
