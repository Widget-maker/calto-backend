package kr.app.calto.infrastructure.oauth

import kr.app.calto.domain.AuthProvider
import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import kr.app.calto.service.dto.OAuthUserInfo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class OAuthUserInfoClient(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    @Qualifier("oAuthRestClient")
    private val restClient: RestClient,
) {

    fun fetch(
        provider: AuthProvider,
        code: String,
    ): OAuthUserInfo {
        val registration =
            clientRegistrationRepository.findByRegistrationId(provider.registrationId())
                ?: throw CalToException(
                    ErrorCode.OAUTH_PROVIDER_NOT_REGISTERED,
                    "등록되지 않은 OAuth 제공자: ${provider.name}",
                )

        val accessToken = exchangeCodeForAccessToken(registration, code)
        val attributes = fetchUserAttributes(registration, accessToken)
        return mapToUserInfo(provider, attributes)
    }

    private fun exchangeCodeForAccessToken(
        registration: ClientRegistration,
        code: String,
    ): String {
        val body =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "authorization_code")
                add("code", code)
                add("client_id", registration.clientId)
                add("redirect_uri", registration.redirectUri)
                if (!registration.clientSecret.isNullOrBlank()) {
                    add("client_secret", registration.clientSecret)
                }
            }

        val raw =
            restClient.post()
                .uri(registration.providerDetails.tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map::class.java)
                ?: throw CalToException(ErrorCode.OAUTH_AUTH_FAILED, "OAuth 토큰 응답이 비어있습니다.")

        @Suppress("UNCHECKED_CAST")
        val response = raw as Map<String, Any?>

        return (response["access_token"] as? String)
            ?: throw CalToException(ErrorCode.OAUTH_AUTH_FAILED, "OAuth 응답에서 access_token을 찾을 수 없습니다.")
    }

    private fun fetchUserAttributes(
        registration: ClientRegistration,
        accessToken: String,
    ): Map<String, Any?> {
        val raw =
            restClient.get()
                .uri(registration.providerDetails.userInfoEndpoint.uri)
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .body(Map::class.java)
                ?: throw CalToException(ErrorCode.OAUTH_AUTH_FAILED, "OAuth 사용자 정보 응답이 비어있습니다.")

        @Suppress("UNCHECKED_CAST")
        return raw as Map<String, Any?>
    }

    private fun mapToUserInfo(
        provider: AuthProvider,
        attributes: Map<String, Any?>,
    ): OAuthUserInfo =
        when (provider) {
            AuthProvider.KAKAO -> mapKakao(attributes)
            AuthProvider.GOOGLE -> mapGoogle(attributes)
        }

    @Suppress("UNCHECKED_CAST")
    private fun mapKakao(attributes: Map<String, Any?>): OAuthUserInfo {
        val providerId =
            (attributes["id"]?.toString())
                ?: throw CalToException(ErrorCode.OAUTH_AUTH_FAILED, "Kakao 응답에 id가 없습니다.")
        val kakaoAccount = attributes["kakao_account"] as? Map<String, Any?> ?: emptyMap()
        val profile = kakaoAccount["profile"] as? Map<String, Any?> ?: emptyMap()
        return OAuthUserInfo(
            provider = AuthProvider.KAKAO,
            providerId = providerId,
            nickname = (profile["nickname"] as? String) ?: "kakao_$providerId",
            profileImageUrl = profile["profile_image_url"] as? String,
        )
    }

    private fun mapGoogle(attributes: Map<String, Any?>): OAuthUserInfo {
        val providerId =
            (attributes["sub"] as? String)
                ?: throw CalToException(ErrorCode.OAUTH_AUTH_FAILED, "Google 응답에 sub가 없습니다.")
        return OAuthUserInfo(
            provider = AuthProvider.GOOGLE,
            providerId = providerId,
            nickname = (attributes["name"] as? String) ?: "google_$providerId",
            profileImageUrl = attributes["picture"] as? String,
        )
    }

    private fun AuthProvider.registrationId(): String = name.lowercase()
}
