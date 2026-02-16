package com.codistrib.authservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {
    // Au moins 12 caractères, >= 1 minuscule, >= 1 majuscule
    private static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{12,}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false; // on refuse null
        return value.matches(REGEX);
    }
}