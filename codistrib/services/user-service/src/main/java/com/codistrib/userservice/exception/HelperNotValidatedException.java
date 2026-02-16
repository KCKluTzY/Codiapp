package com.codistrib.userservice.exception;

import java.util.UUID;

public class HelperNotValidatedException extends RuntimeException {
    
    public HelperNotValidatedException(UUID helperId) {
        super(String.format("Le Helper avec l'ID %s n'est pas validé. " +
                "Veuillez attendre la validation d'un administrateur.", helperId));
    }
    
    public HelperNotValidatedException(String message) {
        super(message);
    }
}