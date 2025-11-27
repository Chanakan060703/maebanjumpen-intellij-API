package com.itsci.mju.maebanjumpen.transaction.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    // Basic CRUD operations: ใช้ DTO
    List<TransactionDTO> getAllTransactions();
    Optional<TransactionDTO> getTransactionById(int id);
    TransactionDTO saveTransaction(TransactionDTO transactionDto);
    void deleteTransaction(int id);

    // Member-specific operations: ใช้ DTO
    List<TransactionDTO> getTransactionsByMemberId(int memberId);

    // Withdrawal-related operations: ใช้ DTO
    List<TransactionDTO> getWithdrawalRequests();
    Optional<TransactionDTO> updateWithdrawalRequestStatus(Integer transactionId, String newStatus);

    // Omise Webhook Processing: ⬅️ เมธอดสำหรับการประมวลผล Webhook
    void processOmiseChargeComplete(JsonNode omisePayload) throws Exception;


    // Status-related utility methods
    String getLocalizedStatus(String status, boolean isEnglish);
    String getStatusColorHex(String status);
}