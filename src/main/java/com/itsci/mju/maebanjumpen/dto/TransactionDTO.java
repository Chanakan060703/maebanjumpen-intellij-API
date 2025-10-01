package com.itsci.mju.maebanjumpen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// 🚨 แก้ไข: เปลี่ยน Import จาก AspectJ ไปเป็น Model ของคุณ
import com.itsci.mju.maebanjumpen.model.Member;

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

    // 🚨 ใช้ Member Object ของ Model
    private Member member;

    private String prompayNumber;
    private String bankAccountNumber;
    private String bankAccountName;
    private LocalDateTime transactionApprovalDate;
}