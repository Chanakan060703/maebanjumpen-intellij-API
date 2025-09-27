package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.TransactionDTO;
import com.itsci.mju.maebanjumpen.service.MemberService;
import com.itsci.mju.maebanjumpen.service.OmiseService;
import com.itsci.mju.maebanjumpen.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/maeban/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final MemberService memberService;
    private final OmiseService omiseService;
    private final ObjectMapper objectMapper;

    // ------------------------------------------------------------------
    // GET MAPPINGS (เหมือนเดิม)
    // ------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Integer id) {
        Optional<TransactionDTO> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(params = "memberId")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByMemberId(@RequestParam Integer memberId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByMemberId(memberId);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}/status")
    public ResponseEntity<Map<String, String>> getTransactionStatus(@PathVariable Integer transactionId) {
        Optional<TransactionDTO> optionalTransaction = transactionService.getTransactionById(transactionId);
        Map<String, String> response = new HashMap<>();

        if (optionalTransaction.isPresent()) {
            TransactionDTO transaction = optionalTransaction.get();
            response.put("transactionId", String.valueOf(transaction.getTransactionId()));
            response.put("transactionStatus", transaction.getTransactionStatus());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Transaction not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ------------------------------------------------------------------
    // POST/PUT/PATCH/DELETE MAPPINGS (เหมือนเดิม)
    // ------------------------------------------------------------------

    @PostMapping // Create
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDto) {
        try {
            TransactionDTO savedTransaction = transactionService.saveTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create transaction: " + e.getMessage(), e);
        }
    }

    @PatchMapping("/{transactionId}/status") // Update Status Endpoint
    public ResponseEntity<Map<String, String>> updateTransactionStatus(
            @PathVariable Integer transactionId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String newStatus = requestBody.get("newStatus");

            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New status is required."));
            }

            Optional<TransactionDTO> updatedTransaction = transactionService.updateWithdrawalRequestStatus(transactionId, newStatus);

            if (updatedTransaction.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Transaction not found."));
            }

            return ResponseEntity.ok(Map.of("message", "Transaction status updated successfully.",
                    "transactionId", String.valueOf(updatedTransaction.get().getTransactionId()),
                    "newStatus", updatedTransaction.get().getTransactionStatus()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update transaction status: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{id}") // Full Update
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Integer id, @RequestBody TransactionDTO transactionDto) {

        if (transactionService.getTransactionById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        transactionDto.setTransactionId(id);

        try {
            TransactionDTO updatedTransaction = transactionService.saveTransaction(transactionDto);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update transaction: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ------------------------------------------------------------------
    // QR Code Generation (ส่วนที่ได้รับการแก้ไข)
    // ------------------------------------------------------------------

    @PostMapping("/qrcode/deposit")
    public ResponseEntity<Map<String, Object>> createDepositQrCode(@RequestBody Map<String, Object> requestBody) {

        // --- ส่วนที่ 1: การดึงและแปลง Member ID/Amount อย่างปลอดภัย ---
        Integer memberId = null;
        Double amount = null;

        try {
            // 1. ดึง memberId
            Object memberIdObj = requestBody.get("memberId");
            if (memberIdObj != null) {
                if (memberIdObj instanceof Number) {
                    memberId = ((Number) memberIdObj).intValue();
                } else if (memberIdObj instanceof String) {
                    memberId = Integer.parseInt((String) memberIdObj);
                }
            }

            // 2. ดึง amount
            Object amountObj = requestBody.get("amount");
            if (amountObj != null) {
                if (amountObj instanceof Number) {
                    amount = ((Number) amountObj).doubleValue();
                } else if (amountObj instanceof String) {
                    amount = Double.parseDouble((String) amountObj);
                }
            }

        } catch (ClassCastException | NumberFormatException e) {
            System.err.println("Type Casting Error in QR Code Request: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid data type for memberId or amount. Please check the request format."));
        }
        // --- สิ้นสุดส่วนที่ 1 ---


        // 3. ตรวจสอบความสมบูรณ์ของข้อมูลที่จำเป็น
        if (memberId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Member ID is missing or invalid in the request body."));
        }
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "A positive amount is required."));
        }

        TransactionDTO depositTransactionDto = null;
        TransactionDTO savedTransactionDto = null;


        try {
            // 4. สร้าง DTO สำหรับ Transaction ใหม่
            depositTransactionDto = new TransactionDTO();
            depositTransactionDto.setMemberId(memberId);
            depositTransactionDto.setTransactionType("DEPOSIT");
            depositTransactionDto.setTransactionAmount(amount);
            depositTransactionDto.setTransactionStatus("Pending Payment");

            // 5. Save Transaction เพื่อให้ได้ ID (Log ยืนยันว่าสำเร็จ)
            savedTransactionDto = transactionService.saveTransaction(depositTransactionDto);

            // 6. เรียก OmiseService (Log ยืนยันว่าสำเร็จ)
            Map<String, String> omiseQrResponse = omiseService.createPromptPayQRCode(
                    amount,
                    String.valueOf(savedTransactionDto.getTransactionId())
            );

            if (omiseQrResponse != null && omiseQrResponse.containsKey("qrCodeImageBase64")) {
                String svgBase64 = omiseQrResponse.get("qrCodeImageBase64");

                // 7. Update Transaction Status: QR Generated (🎯 จุดแก้ไขปัญหา 400)
                savedTransactionDto.setTransactionStatus("QR Generated");

                // 💥 FIX: รับประกันว่า memberId ไม่หายไปก่อนบันทึกครั้งที่ 2
                // (ใช้ตัวแปร memberId ที่ดึงมาจาก Request Body)
                if (savedTransactionDto.getMemberId() == null) {
                    savedTransactionDto.setMemberId(memberId);
                }

                // Save the updated transaction (Log น่าจะผ่านจุดนี้แล้ว)
                transactionService.saveTransaction(savedTransactionDto);

                Map<String, Object> response = new HashMap<>();
                response.put("transactionId", savedTransactionDto.getTransactionId());
                response.put("qrCodeImageBase64", svgBase64);

                return ResponseEntity.ok(response); // ส่ง 200 OK กลับไป
            } else {
                // 8. Handle Omise Failure (API Success แต่ไม่มี QR Data)
                if (savedTransactionDto != null) {
                    savedTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransactionDto);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to generate QR Code from Omise API or no QR data returned."));
            }
        } catch (IllegalArgumentException e) {
            // 9. Handle OmiseService/TransactionService Error (เช่น Minimum amount, Invalid Member ID)
            if (savedTransactionDto != null) {
                try {
                    savedTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransactionDto);
                } catch (Exception ex) { /* ignore secondary save error */ }
            } else if (depositTransactionDto != null && depositTransactionDto.getTransactionId() != null) {
                try {
                    depositTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(depositTransactionDto);
                } catch (Exception ex) { /* ignore secondary save error */ }
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // 10. Handle Omise API Network/HTTP Error
            if (savedTransactionDto != null) {
                try {
                    savedTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransactionDto);
                } catch (Exception ex) { /* ignore secondary save error */ }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Omise API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            // 11. Handle General Error
            if (savedTransactionDto != null) {
                try {
                    savedTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransactionDto);
                } catch (Exception ex) { /* ignore secondary save error */ }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating deposit QR code: " + e.getMessage(), e);
        }
    }
}
