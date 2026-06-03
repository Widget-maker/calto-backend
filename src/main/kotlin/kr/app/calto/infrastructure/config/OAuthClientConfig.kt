package kr.app.calto.infrastructure.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class OAuthClientConfig {
    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository::class)
    fun emptyClientRegistrationRepository(): ClientRegistrationRepository = ClientRegistrationRepository { null }
}
