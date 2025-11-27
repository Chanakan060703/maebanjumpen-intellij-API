package com.itsci.mju.maebanjumpen.other.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO ที่ใช้ส่ง Error Response แบบมีโครงสร้างกลับไปให้ Client
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    // รหัสข้อผิดพลาด เช่น "ACCOUNT_RESTRICTED", "INVALID_CREDENTIALS"
    private String errorCode;

    // สถานะบัญชีจริงที่ทำให้เข้าสู่ระบบไม่ได้ (เช่น "Ban", "Account Suspension", "PENDING")
    private String accountStatus;

    // ข้อความอธิบายที่ใช้เป็น Fallback
    private String message;
}
