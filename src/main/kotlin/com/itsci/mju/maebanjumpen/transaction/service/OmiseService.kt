package com.itsci.mju.maebanjumpen.transaction.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.config.OmiseAPIConfig
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class OmiseService(
    private val omiseAPIConfig: OmiseAPIConfig,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {

    fun createPromptPayQRCode(amount: Double, transactionId: String): Map<String, Any>? {
        val url = "${omiseAPIConfig.baseUrl}/sources"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            val auth = "${omiseAPIConfig.secretKey}:"
            val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
            set("Authorization", "Basic $encodedAuth")
        }

        val amountInSatang = (amount * 100).toLong()

        val requestBody = mapOf(
            "type" to "promptpay",
            "amount" to amountInSatang,
            "currency" to "THB",
            "metadata" to mapOf("transaction_id" to transactionId)
        )

        val entity = HttpEntity(requestBody, headers)

        return try {
            val response = restTemplate.exchange(url, HttpMethod.POST, entity, String::class.java)

            if (response.statusCode == HttpStatus.OK && response.body != null) {
                @Suppress("UNCHECKED_CAST")
                val responseMap = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>

                val scannable = responseMap["scannable_code"] as? Map<*, *>
                val image = scannable?.get("image") as? Map<*, *>
                val svgData = image?.get("download_uri") as? String

                if (svgData != null) {
                    val svgResponse = restTemplate.getForEntity(svgData, ByteArray::class.java)
                    if (svgResponse.statusCode == HttpStatus.OK && svgResponse.body != null) {
                        val base64Svg = Base64.getEncoder().encodeToString(svgResponse.body)
                        return mapOf(
                            "sourceId" to (responseMap["id"] ?: ""),
                            "qrCodeImageBase64" to "data:image/svg+xml;base64,$base64Svg"
                        )
                    }
                }
                responseMap
            } else {
                null
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to create PromptPay QR Code: ${e.message}", e)
        }
    }

    fun verifyWebhookSignature(payload: String, signature: String): Boolean {
        // Implement webhook signature verification if needed
        return true
    }
}

