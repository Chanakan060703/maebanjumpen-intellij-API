package com.itsci.mju.maebanjumpen.entity

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

object PasswordUtil {
    fun hashPassword(password: String?): String {
        requireNotNull(password) { "Password cannot be null" }
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
            Base64.getEncoder().encodeToString(hash)
        } catch (e: Exception) {
            throw RuntimeException("Failed to hash password", e)
        }
    }

    fun verifyPassword(inputPassword: String?, storedHash: String?): Boolean {
        if (inputPassword == null || storedHash == null) {
            return false
        }
        val hashedInput = hashPassword(inputPassword)
        return hashedInput == storedHash
    }
}

