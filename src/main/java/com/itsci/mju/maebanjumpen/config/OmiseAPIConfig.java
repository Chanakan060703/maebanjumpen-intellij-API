package com.itsci.mju.maebanjumpen.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // เพิ่ม import นี้
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // เพิ่ม import นี้
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:application.properties")
public class OmiseAPIConfig {

    @Value("${omise.api.public-key}")
    private String publicKey;

    @Value("${omise.api.secret-key}")
    private String secretKey;

    @Value("${omise.api.base-url}")
    private String baseUrl;

    @Value("${omise.webhook.url}")
    private String webhookUrl;

    public String getPublicKey() {
        return publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // === เพิ่มโค้ด 2 บรรทัดนี้ ===
        objectMapper.registerModule(new JavaTimeModule()); // ลงทะเบียนโมดูลสำหรับ Java 8 Date/Time API
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // (Optional) ทำให้ Date/Time ถูก serialize เป็น String (ISO 8601) แทน Timestamp
        // ============================
        return objectMapper;
    }
}