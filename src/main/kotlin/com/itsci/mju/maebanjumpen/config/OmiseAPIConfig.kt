package com.itsci.mju.maebanjumpen.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class OmiseAPIConfig {

    @Value("\${omise.api.public-key}")
    lateinit var publicKey: String

    @Value("\${omise.api.secret-key}")
    lateinit var secretKey: String

    @Value("\${omise.api.base-url}")
    lateinit var baseUrl: String

    @Value("\${omise.webhook.url}")
    lateinit var webhookUrl: String

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}

