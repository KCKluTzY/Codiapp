package com.codistrib.authservice.dto;

import com.codistrib.authservice.domain.enums.UserRole;
import com.codistrib.authservice.validation.PasswordPolicy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record RegisterCmd(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Email String email,
            @NotBlank @PasswordPolicy String password, UserRole role) {}

    public record LoginCmd(
            @NotBlank String identifier,
            @NotBlank String password) {}

    public record TokenPair(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            String userId,
            UserRole role,
            String username,
            String email) {}
}