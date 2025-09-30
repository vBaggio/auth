package com.auth.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDTO(
    Long id,
    String email,
    String firstName,
    String lastName,
    Set<RoleDTO> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}