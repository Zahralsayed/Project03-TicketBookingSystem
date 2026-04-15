package com.ga.TicketSystem.service;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class TokenService {
    private static final int EXPIRE_MINUTES = 15;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a secure, URL-safe random string.
     */
    public String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Checks if a token is expired.
     * Returns true if expired, false if still valid.
     */
    public boolean isTokenExpired(LocalDateTime createdAt) {
        if (createdAt == null) return true;
        return createdAt.plusMinutes(EXPIRE_MINUTES).isBefore(LocalDateTime.now());
    }
}
