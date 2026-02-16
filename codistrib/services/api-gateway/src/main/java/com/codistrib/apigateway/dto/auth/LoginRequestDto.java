package com.codistrib.apigateway.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de connexion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    @NotBlank(message = "L'identifiant (email ou username) est requis")
    private String identifier;
    
    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}