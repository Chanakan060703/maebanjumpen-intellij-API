package com.itsci.mju.maebanjumpen.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null) {
            return false;
        }
        String hashedInput = hashPassword(inputPassword);
        return hashedInput.equals(storedHash);
    }
}