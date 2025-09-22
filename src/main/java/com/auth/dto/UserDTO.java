package com.auth.dto;

import com.auth.entity.Role;
import java.time.LocalDateTime;
import java.util.Set;

public record UserDTO(
    Long id,
    String email,
    String firstName,
    String lastName,
    Set<Role.RoleName> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}