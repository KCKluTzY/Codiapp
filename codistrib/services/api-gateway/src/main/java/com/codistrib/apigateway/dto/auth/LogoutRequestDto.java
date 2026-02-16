package com.codistrib.apigateway.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de déconnexion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto {
    
    @NotBlank(message = "L'access token est requis")
    private String accessToken;
    
    @NotBlank(message = "Le refresh token est requis")
    private String refreshToken;
}