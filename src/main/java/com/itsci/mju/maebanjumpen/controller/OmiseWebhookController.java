package com.itsci.mju.maebanjumpen.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsci.mju.maebanjumpen.service.TransactionService; // ⬅️ ต้องสร้าง/อ้างอิงถึง TransactionService
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // ❌ @Transactional ควรอยู่บน Service Layer

@RestController
@RequestMapping("/webhook/omise")
@RequiredArgsConstructor // ⬅️ ใช้ Lombok เพื่อจัดการ Constructor Injection
public class OmiseWebhookController {

    private final ObjectMapper objectMapper;
    private final TransactionService transactionService; // ⬅️ Inject TransactionService

    // ❌ ลบ Repositories ออกจาก Controller
    // private final TransactionRepository transactionRepository;
    // private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<String> handleOmiseWebhook(@RequestBody String payload) {
        // Log ข้อมูลที่ได้รับ (สามารถย้ายไปใน Service ได้ถ้าต้องการ)
        System.out.println("Received Omise Webhook Payload: " + payload);

        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("key").asText();

            if ("charge.complete".equals(eventType)) {
                // ➡️ ส่งตรรกะการประมวลผลทั้งหมดเข้าสู่ Service Layer
                transactionService.processOmiseChargeComplete(root);
                return new ResponseEntity<>("Webhook processed successfully.", HttpStatus.OK);

            } else {
                // คืนค่า 200 OK สำหรับ Event Type อื่นๆ เพื่อยืนยันว่า Webhook ได้รับแล้ว
                System.out.println("Ignoring Omise Webhook Event Type: " + eventType);
                return new ResponseEntity<>("Event type not handled.", HttpStatus.OK);
            }

        } catch (Exception e) {
            System.err.println("Error processing Omise Webhook: " + e.getMessage());
            e.printStackTrace();
            // คืนค่า 500 INTERNAL_SERVER_ERROR เพื่อให้ Omise พยายามส่งซ้ำ (Retry)
            return new ResponseEntity<>("Error processing webhook: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}