package com.itsci.mju.maebanjumpen.exception;

// สร้าง Exception ใหม่ เพื่อใช้เมื่อบัญชีถูกจำกัดการเข้าถึง (แต่รหัสผ่านถูกต้อง)
public class AccountStatusException extends RuntimeException {
    private final String accountStatus;

    public AccountStatusException(String message, String accountStatus) {
        super(message);
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
