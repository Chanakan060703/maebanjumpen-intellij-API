package com.itsci.mju.maebanjumpen.other.dto

// DTO ที่ใช้ส่ง Error Response แบบมีโครงสร้างกลับไปให้ Client
data class ErrorResponseDTO(
    // รหัสข้อผิดพลาด เช่น "ACCOUNT_RESTRICTED", "INVALID_CREDENTIALS"
    var errorCode: String? = null,
    // สถานะบัญชีจริงที่ทำให้เข้าสู่ระบบไม่ได้ (เช่น "Ban", "Account Suspension", "PENDING")
    var accountStatus: String? = null,
    // ข้อความอธิบายที่ใช้เป็น Fallback
    var message: String? = null
)

