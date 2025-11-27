package com.itsci.mju.maebanjumpen.transaction.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO;
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

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    private MemberDTO member;

    private String prompayNumber;
    private String bankAccountNumber;
    private String bankAccountName;
    private LocalDateTime transactionApprovalDate;
}
