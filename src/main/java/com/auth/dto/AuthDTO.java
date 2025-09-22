package com.auth.dto;

import java.time.LocalDateTime;

public record AuthDTO(
    String token,
    String type,
    String email,
    String firstName,
    String lastName,
    LocalDateTime expiresAt
) {
    public AuthDTO(String token, String email, String firstName, String lastName, LocalDateTime expiresAt) {
        this(token, "Bearer", email, firstName, lastName, expiresAt);
    }
}