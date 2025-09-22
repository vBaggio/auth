package com.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ErrorDTO(
    String message,
    String error,
    int status,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp,
    String path
) {
    public ErrorDTO(String message, String error, int status, String path) {
        this(message, error, status, LocalDateTime.now(), path);
    }
}