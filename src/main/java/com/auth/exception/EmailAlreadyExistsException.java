package com.auth.exception;

public class EmailAlreadyExistsException extends AuthException {
    
    public EmailAlreadyExistsException(String message) {
        super(message, "EMAIL_ALREADY_EXISTS");
    }
    
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, "EMAIL_ALREADY_EXISTS", cause);
    }
}
