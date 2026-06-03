package kr.app.calto.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class HttpClientConfig {
    @Bean
    fun oAuthRestClient(): RestClient = RestClient.builder().build()
}
