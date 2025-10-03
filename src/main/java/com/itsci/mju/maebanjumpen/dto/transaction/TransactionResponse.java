package com.itsci.mju.maebanjumpen.dto.transaction;

import java.time.LocalDateTime;

record TransactionResponse(
        Long transactionId,
        Long memberId, // อาจใช้ Long แทน MemberResponse เพื่อลดความซับซ้อน
        Double transactionAmount,
        String transactionType,
        String transactionStatus,
        LocalDateTime transactionDate,
        LocalDateTime transactionApprovalDate
) { }
