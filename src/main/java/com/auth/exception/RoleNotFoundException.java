package com.auth.exception;

public class RoleNotFoundException extends AuthException {
    
    public RoleNotFoundException(String message) {
        super(message, "ROLE_NOT_FOUND");
    }
    
    public RoleNotFoundException(String message, Throwable cause) {
        super(message, "ROLE_NOT_FOUND", cause);
    }
}
