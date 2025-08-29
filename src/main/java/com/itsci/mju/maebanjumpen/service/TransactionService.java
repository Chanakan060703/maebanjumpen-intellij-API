package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    // Basic CRUD operations
    List<Transaction> getAllTransactions();
    Optional<Transaction> getTransactionById(int id);
    Transaction saveTransaction(Transaction transaction);
    void deleteTransaction(int id);

    // Member-specific operations
    List<Transaction> getTransactionsByMemberId(int memberId);

    // Withdrawal-related operations
    List<Transaction> getWithdrawalRequests();
    Optional<Transaction> updateWithdrawalRequestStatus(Integer transactionId, String newStatus);

    // Status-related utility methods
    String getLocalizedStatus(String status, boolean isEnglish);
    String getStatusColorHex(String status);
}