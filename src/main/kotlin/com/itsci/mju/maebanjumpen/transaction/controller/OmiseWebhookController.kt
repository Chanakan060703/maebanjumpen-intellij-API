package com.itsci.mju.maebanjumpen.transaction.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.transaction.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook/omise")
class OmiseWebhookController(
    private val objectMapper: ObjectMapper,
    private val transactionService: TransactionService
) {

    @PostMapping
    fun handleOmiseWebhook(@RequestBody payload: String): ResponseEntity<String> {
        println("Received Omise Webhook Payload: $payload")

        return try {
            val root = objectMapper.readTree(payload)
            val eventType = root.path("key").asText()

            if (eventType == "charge.complete") {
                transactionService.processOmiseChargeComplete(root)
                ResponseEntity("Webhook processed successfully.", HttpStatus.OK)
            } else {
                // คืนค่า 200 OK สำหรับ Event Type อื่นๆ เพื่อยืนยันว่า Webhook ได้รับแล้ว
                println("Ignoring Omise Webhook Event Type: $eventType")
                ResponseEntity("Event type not handled.", HttpStatus.OK)
            }
        } catch (e: Exception) {
            System.err.println("Error processing Omise Webhook: ${e.message}")
            e.printStackTrace()
            // คืนค่า 500 INTERNAL_SERVER_ERROR เพื่อให้ Omise พยายามส่งซ้ำ (Retry)
            ResponseEntity("Error processing webhook: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}

