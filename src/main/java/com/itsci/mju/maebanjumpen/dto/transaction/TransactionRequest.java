package com.itsci.mju.maebanjumpen.dto.transaction;

record TransactionRequest(
        Long transactionId, // ใช้สำหรับอัปเดต (ถ้ามี)
        MemberRequest member, // ต้องใช้ Nested MemberRequest เพื่อให้ตรงกับ DTO/Entity
        Double transactionAmount,
        String transactionType,
        String prompayNumber,
        String bankAccountNumber,
        String bankAccountName,
        String transactionStatus // สถานะเริ่มต้น
) { }
