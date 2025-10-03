package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.TransactionDTO;
import com.itsci.mju.maebanjumpen.dto.MemberDTO;
import com.itsci.mju.maebanjumpen.dto.QrCodeRequestDTO;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.service.MemberService;
import com.itsci.mju.maebanjumpen.service.OmiseService;
import com.itsci.mju.maebanjumpen.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
// ⚠️ Note: Removed "/transactions" from RequestMapping to match the POST mapping in the log (POST "/maeban/transactions")
@RequestMapping("/maeban")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final MemberService memberService;
    private final OmiseService omiseService;
    private final ObjectMapper objectMapper;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Integer id) {
        Optional<TransactionDTO> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/transactions", params = "memberId")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByMemberId(@RequestParam Integer memberId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByMemberId(memberId);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/{transactionId}/status")
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

    /**
     * FIX: เปลี่ยน Argument เป็น TransactionDTO โดยตรง เพื่อให้ Jackson จัดการ Data Binding
     * ทั้ง Member Object และ Date/Time ได้อย่างถูกต้อง
     */
    @PostMapping("/transactions") // Create
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDto) {
        try {
            // 1. ตรวจสอบว่า DTO ถูกแปลงสำเร็จแล้วและมี Member ID
            if (transactionDto.getMember() == null || transactionDto.getMember().getId() == null) {
                throw new IllegalArgumentException("Member ID is missing or invalid in the request.");
            }

            // 2. ถ้า DTO ถูกแปลงสำเร็จ (รวมถึง Date/Time) ก็เรียก Service ได้เลย
            TransactionDTO savedTransaction = transactionService.saveTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);

        } catch (IllegalArgumentException e) {
            // 400 Bad Request สำหรับ Input Validation
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            // ใช้ 400 Bad Request สำหรับ Runtime errors (เช่น DB constraints)
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // 500 Internal Server Error สำหรับข้อผิดพลาดที่ไม่คาดคิด
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create transaction: " + e.getMessage(), e);
        }
    }

    @PatchMapping("/transactions/{transactionId}/status")
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

    @PutMapping("/transactions/{id}") // Full Update
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

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Refactored to use QrCodeRequestDTO to automatically handle JSON parsing,
     * removing complex manual type casting logic.
     * FIX: Added "/transactions" to match the path expected by the Flutter client.
     */
    @PostMapping("/transactions/qrcode/deposit") // FIX: Added "/transactions"
    public ResponseEntity<Map<String, Object>> createDepositQrCode(@RequestBody QrCodeRequestDTO request) {

        Integer memberId = request.getMemberId();
        Double amount = request.getAmount();

        if (memberId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Member ID is missing or invalid in the request body."));
        }
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "A positive amount is required."));
        }

        TransactionDTO depositTransactionDto = null;
        TransactionDTO savedTransactionDto = null;


        try {
            // 1. สร้าง Transaction สำหรับการฝากเงิน (DEPOSIT)
            depositTransactionDto = new TransactionDTO();
            MemberDTO memberDtoStub = new MemberDTO();
            memberDtoStub.setId(memberId);
            depositTransactionDto.setMember(memberDtoStub);
            depositTransactionDto.setTransactionType("DEPOSIT");
            depositTransactionDto.setTransactionAmount(amount);
            depositTransactionDto.setTransactionStatus("Pending Payment");

            // 2. บันทึก Transaction เพื่อให้ได้ ID สำหรับใช้กับ Omise
            savedTransactionDto = transactionService.saveTransaction(depositTransactionDto);

            // 3. เรียก Omise Service เพื่อสร้าง QR Code
            Map<String, String> omiseQrResponse = omiseService.createPromptPayQRCode(
                    amount,
                    String.valueOf(savedTransactionDto.getTransactionId())
            );

            if (omiseQrResponse != null && omiseQrResponse.containsKey("qrCodeImageBase64")) {
                String svgBase64 = omiseQrResponse.get("qrCodeImageBase64");

                // 4. อัปเดตสถานะ Transaction เมื่อ QR Code ถูกสร้างแล้ว
                savedTransactionDto.setTransactionStatus("QR Generated");
                transactionService.saveTransaction(savedTransactionDto);

                Map<String, Object> response = new HashMap<>();
                response.put("transactionId", savedTransactionDto.getTransactionId());
                response.put("qrCodeImageBase64", svgBase64);
                return ResponseEntity.ok(response);
            } else {
                if (savedTransactionDto != null) {
                    savedTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransactionDto);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to generate QR Code from Omise API or no QR data returned."));
            }
        } catch (IllegalArgumentException e) {
            // Handle OmiseService's IllegalArgumentException (e.g., minimum amount)
            TransactionDTO failedDto = (savedTransactionDto != null) ? savedTransactionDto : depositTransactionDto;
            if (failedDto != null && failedDto.getTransactionId() != null) {
                try {
                    failedDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(failedDto);
                } catch (Exception ex) { /* ignore secondary save error */ }
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (savedTransactionDto != null) {
                try {
                    savedTransactionDto.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransactionDto);
                } catch (Exception ex) { /* ignore secondary save error */ }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Omise API Error: " + e.getMessage(), e);
        } catch (Exception e) {
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
