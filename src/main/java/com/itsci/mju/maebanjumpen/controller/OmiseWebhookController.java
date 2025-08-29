package com.itsci.mju.maebanjumpen.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.model.Transaction;
import com.itsci.mju.maebanjumpen.repository.MemberRepository;
import com.itsci.mju.maebanjumpen.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/webhook/omise") // Path สำหรับรับ Webhook
public class OmiseWebhookController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> handleOmiseWebhook(@RequestBody String payload) {
        System.out.println("Received Omise Webhook Payload: " + payload);

        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("key").asText(); // เช่น "charge.complete", "charge.create"
            String chargeId = root.path("data").path("id").asText(); // Omise charge ID
            String chargeStatus = root.path("data").path("status").asText(); // เช่น "successful", "failed"
            boolean paid = root.path("data").path("paid").asBoolean(); // true/false
            double amountInSatang = root.path("data").path("amount").asDouble(); // จำนวนเงินใน satang
            String currency = root.path("data").path("currency").asText(); // สกุลเงิน
            String ourTransactionId = root.path("data").path("metadata").path("transaction_id").asText(); // transaction_id ของเรา

            System.out.println("Omise Webhook Event Type: " + eventType);
            System.out.println("Omise Charge ID: " + chargeId);
            System.out.println("Omise Charge Status: " + chargeStatus);
            System.out.println("Paid: " + paid);
            System.out.println("Amount (Satang): " + amountInSatang);
            System.out.println("Our Transaction ID: " + ourTransactionId);

            if ("charge.complete".equals(eventType)) {
                Optional<Transaction> optionalTransaction = transactionRepository.findById(Integer.parseInt(ourTransactionId));

                if (optionalTransaction.isPresent()) {
                    Transaction transaction = optionalTransaction.get();

                    if ("successful".equals(chargeStatus) && paid) {
                        transaction.setTransactionStatus("SUCCESS");
                        transaction.setTransactionApprovalDate(LocalDateTime.now());

                        Member member = transaction.getMember();
                        if (member != null) {
                            double amountInBaht = amountInSatang / 100.0;
                            double currentMemberBalance = member.getBalance() != null ? member.getBalance() : 0.0;
                            member.setBalance(currentMemberBalance + amountInBaht);
                            memberRepository.save(member);
                            System.out.println("Member ID: " + member.getId() + " balance updated to: " + member.getBalance());
                        } else {
                            System.err.println("Error: Member not found for transaction ID: " + ourTransactionId);
                            return new ResponseEntity<>("Member associated with transaction not found.", HttpStatus.BAD_REQUEST);
                        }

                    } else if ("failed".equals(chargeStatus)) {
                        transaction.setTransactionStatus("FAILED");
                        System.out.println("Transaction ID: " + ourTransactionId + " failed.");
                    } else {
                        transaction.setTransactionStatus(chargeStatus.toUpperCase());
                        System.out.println("Transaction ID: " + ourTransactionId + " status: " + chargeStatus);
                    }

                    transactionRepository.save(transaction);
                    return new ResponseEntity<>("Webhook processed successfully.", HttpStatus.OK);

                } else {
                    System.err.println("Transaction with ID " + ourTransactionId + " not found in our system.");
                    return new ResponseEntity<>("Transaction not found.", HttpStatus.NOT_FOUND);
                }
            } else {
                System.out.println("Ignoring Omise Webhook Event Type: " + eventType);
                return new ResponseEntity<>("Event type not handled.", HttpStatus.OK);
            }

        } catch (Exception e) {
            System.err.println("Error processing Omise Webhook: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error processing webhook.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}