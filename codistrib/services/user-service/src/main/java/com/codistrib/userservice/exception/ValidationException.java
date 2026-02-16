package com.codistrib.userservice.exception;

public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String issue) {
        super(String.format("Validation échouée pour '%s': %s", field, issue));
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}