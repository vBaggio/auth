package com.auth.mapper;

import com.auth.dto.AuthDTO;
import com.auth.entity.User;

import java.time.LocalDateTime;

public class AuthMapper {
    
    public static AuthDTO toAuthDTO(User user, String token, LocalDateTime expiresAt) {
        if (user == null) {
            return null;
        }
        return new AuthDTO(token, user.getEmail(), user.getFirstName(), user.getLastName(), expiresAt);
    }
    
}
