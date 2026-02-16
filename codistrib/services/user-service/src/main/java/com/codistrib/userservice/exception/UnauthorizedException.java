package com.codistrib.userservice.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String action, String role) {
        super(String.format("Action '%s' non autorisée pour le rôle '%s'", action, role));
    }
}