package com.itsci.mju.maebanjumpen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Integer transactionId;
    private String transactionType;
    private Double transactionAmount;
    private LocalDateTime transactionDate;
    private String transactionStatus;
    private Integer memberId; // Use ID
    private String prompayNumber;
    private String bankAccountNumber;
    private String bankAccountName;
    private LocalDateTime transactionApprovalDate;
}