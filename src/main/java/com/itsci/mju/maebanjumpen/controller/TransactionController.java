package com.itsci.mju.maebanjumpen.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.model.Transaction;
import com.itsci.mju.maebanjumpen.service.MemberService;
import com.itsci.mju.maebanjumpen.service.OmiseService;
import com.itsci.mju.maebanjumpen.service.TransactionService;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/maeban/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OmiseService omiseService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        try {
            if (transaction.getTransactionDate() == null) {
                transaction.setTransactionDate(LocalDateTime.now());
            }
            if (transaction.getTransactionStatus() == null || transaction.getTransactionStatus().isEmpty()) {
                transaction.setTransactionStatus("Pending");
            }

            Transaction savedTransaction = transactionService.saveTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/{transactionId}/status")
    public ResponseEntity<Map<String, String>> updateTransactionStatus(
            @PathVariable Integer transactionId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String newStatus = requestBody.get("newStatus");
            String accountManagerIdStr = requestBody.get("accountManagerId");

            if (newStatus == null || newStatus.isEmpty() || accountManagerIdStr == null || accountManagerIdStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New status and accountManagerId are required."));
            }

            Integer accountManagerId = Integer.parseInt(accountManagerIdStr);
            Optional<Transaction> optionalTransaction = transactionService.getTransactionById(transactionId);

            if (optionalTransaction.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Transaction not found."));
            }

            Transaction transaction = optionalTransaction.get();
            transaction.setTransactionStatus(newStatus);

            if ("Approved".equalsIgnoreCase(newStatus)) {
                transaction.setTransactionApprovalDate(LocalDateTime.now());
            }

            transactionService.saveTransaction(transaction);
            return ResponseEntity.ok(Map.of("message", "Transaction status updated successfully."));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid accountManagerId format."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update transaction status: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Integer id, @RequestBody Transaction transactionDetails) {
        Optional<Transaction> optionalTransaction = transactionService.getTransactionById(id);
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction existingTransaction = optionalTransaction.get();

        if (transactionDetails.getTransactionType() != null) {
            existingTransaction.setTransactionType(transactionDetails.getTransactionType());
        }
        if (transactionDetails.getTransactionAmount() != null) {
            existingTransaction.setTransactionAmount(transactionDetails.getTransactionAmount());
        }
        if (transactionDetails.getTransactionDate() != null) {
            existingTransaction.setTransactionDate(transactionDetails.getTransactionDate());
        }
        if (transactionDetails.getPrompayNumber() != null) {
            existingTransaction.setPrompayNumber(transactionDetails.getPrompayNumber());
        }
        if (transactionDetails.getBankAccountNumber() != null) {
            existingTransaction.setBankAccountNumber(transactionDetails.getBankAccountNumber());
        }
        if (transactionDetails.getBankAccountName() != null) {
            existingTransaction.setBankAccountName(transactionDetails.getBankAccountName());
        }

        Transaction updatedTransaction = transactionService.saveTransaction(existingTransaction);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        if (!transactionService.getTransactionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "memberId")
    public ResponseEntity<List<Transaction>> getTransactionsByMemberId(@RequestParam Integer memberId) {
        List<Transaction> transactions = transactionService.getTransactionsByMemberId(memberId);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/qrcode/deposit")
    public ResponseEntity<Map<String, Object>> createDepositQrCode(@RequestBody Map<String, Object> requestBody) {
        Integer memberId = (Integer) requestBody.get("memberId");
        Double amount = ((Number) requestBody.get("amount")).doubleValue();

        if (memberId == null || amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Member ID and a positive amount are required."));
        }

        Transaction savedTransaction = null;

        try {
            Transaction depositTransaction = new Transaction();
            Member member = memberService.getMemberById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
            depositTransaction.setMember(member);
            depositTransaction.setTransactionType("DEPOSIT");
            depositTransaction.setTransactionAmount(amount);
            depositTransaction.setTransactionDate(LocalDateTime.now());
            depositTransaction.setTransactionStatus("Pending Payment");

            savedTransaction = transactionService.saveTransaction(depositTransaction);

            Map<String, String> omiseQrResponse = omiseService.createPromptPayQRCode(
                    amount,
                    String.valueOf(savedTransaction.getTransactionId())
            );

            if (omiseQrResponse != null && omiseQrResponse.containsKey("qrCodeImageBase64")) {
                String svgBase64 = omiseQrResponse.get("qrCodeImageBase64");

                // ไม่ต้องทำการแปลง SVG เป็น PNG อีกต่อไป

                Map<String, Object> response = new HashMap<>();
                response.put("transactionId", savedTransaction.getTransactionId());
                response.put("qrCodeImageBase64", svgBase64); // ส่ง SVG กลับไปตรงๆ

                savedTransaction.setTransactionStatus("QR Generated");
                transactionService.saveTransaction(savedTransaction);

                return ResponseEntity.ok(response);
            } else {
                System.err.println("OmiseService did not return qrCodeImageBase64. Full response from OmiseService: " + omiseQrResponse);
                if (savedTransaction != null) {
                    savedTransaction.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransaction);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to generate QR Code from Omise API or no QR data returned."));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation/Argument Error: " + e.getMessage());
            if (savedTransaction != null) {
                savedTransaction.setTransactionStatus("Failed");
                transactionService.saveTransaction(savedTransaction);
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error generating deposit QR code: " + e.getMessage());
            e.printStackTrace();
            if (savedTransaction != null) {
                try {
                    savedTransaction.setTransactionStatus("Failed");
                    transactionService.saveTransaction(savedTransaction);
                } catch (Exception ex) {
                    System.err.println("Error saving transaction status to Failed after QR generation error: " + ex.getMessage());
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error generating deposit QR code: " + e.getMessage()));
        }
    }

    @GetMapping("/{transactionId}/status")
    public ResponseEntity<Map<String, String>> getTransactionStatus(@PathVariable Integer transactionId) {
        Optional<Transaction> optionalTransaction = transactionService.getTransactionById(transactionId);
        Map<String, String> response = new HashMap<>();

        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            response.put("transactionId", String.valueOf(transaction.getTransactionId()));
            response.put("transactionStatus", transaction.getTransactionStatus());
            System.out.println("DEBUG: getTransactionStatus for ID " + transactionId + " -> Status: " + transaction.getTransactionStatus());
            return ResponseEntity.ok(response);
        } else {
            System.err.println("DEBUG: getTransactionStatus for ID " + transactionId + " -> Not Found.");
            response.put("error", "Transaction not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}