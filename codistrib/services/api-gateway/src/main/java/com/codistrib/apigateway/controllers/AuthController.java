package com.codistrib.apigateway.controllers;

import com.codistrib.apigateway.dto.auth.*;
import com.codistrib.apigateway.grpc.AuthServiceGrpcClient;
import com.codistrib.proto.auth.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST pour les opérations d'authentification.
 zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz
 * Reçoit les requêtes JSON et les transmet au auth-service via gRPC.
 * 
 * Endpoints :
 * - POST /api/v1/auth/register  - Inscription
 * - POST /api/v1/auth/login     - Connexion
 * - POST /api/v1/auth/refresh   - Rafraîchir le token
 * - POST /api/v1/auth/logout    - Déconnexion
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceGrpcClient authServiceClient;

    /**
     * Inscription d'un nouvel utilisateur.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("POST /api/v1/auth/register - username: {}, email: {}", 
            request.getUsername(), request.getEmail());
        
        try {
            // Convertir le rôle string en enum
            UserRole role = convertRole(request.getRole());
            
            // Construire la requête gRPC
            RegisterRequest grpcRequest = RegisterRequest.newBuilder()
                .setUsername(request.getUsername())
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setRole(role)
                .build();
            
            // Appeler le service gRPC
            TokenResponse grpcResponse = authServiceClient.register(grpcRequest);
            
            // Convertir la réponse en DTO
            TokenResponseDto response = convertTokenResponse(grpcResponse);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (StatusRuntimeException e) {
            return handleGrpcError(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Bad Request",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Connexion d'un utilisateur.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("POST /api/v1/auth/login - identifier: {}", request.getIdentifier());
        
        try {
            // Construire la requête gRPC
            LoginRequest grpcRequest = LoginRequest.newBuilder()
                .setIdentifier(request.getIdentifier())
                .setPassword(request.getPassword())
                .build();
            
            // Appeler le service gRPC
            TokenResponse grpcResponse = authServiceClient.login(grpcRequest);
            
            // Convertir la réponse en DTO
            TokenResponseDto response = convertTokenResponse(grpcResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (StatusRuntimeException e) {
            return handleGrpcError(e);
        }
    }

    /**
     * Rafraîchissement du token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("POST /api/v1/auth/refresh");
        
        try {
            // Construire la requête gRPC
            RefreshTokenRequest grpcRequest = RefreshTokenRequest.newBuilder()
                .setRefreshToken(request.getRefreshToken())
                .build();
            
            // Appeler le service gRPC
            TokenResponse grpcResponse = authServiceClient.refreshToken(grpcRequest);
            
            // Convertir la réponse en DTO
            TokenResponseDto response = convertTokenResponse(grpcResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (StatusRuntimeException e) {
            return handleGrpcError(e);
        }
    }

    /**
     * Déconnexion d'un utilisateur.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequestDto request) {
        log.info("POST /api/v1/auth/logout");
        
        try {
            // Construire la requête gRPC
            LogoutRequest grpcRequest = LogoutRequest.newBuilder()
                .setAccessToken(request.getAccessToken())
                .setRefreshToken(request.getRefreshToken())
                .build();
            
            // Appeler le service gRPC
            LogoutResponse grpcResponse = authServiceClient.logout(grpcRequest);
            
            // Construire la réponse
            LogoutResponseDto response = LogoutResponseDto.builder()
                .success(grpcResponse.getSuccess())
                .message(grpcResponse.getSuccess() ? "Déconnexion réussie" : "Échec de la déconnexion")
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (StatusRuntimeException e) {
            return handleGrpcError(e);
        }
    }

    /**
     * Convertit un rôle string en enum UserRole.
     */
    private UserRole convertRole(String role) {
        return switch (role.toUpperCase()) {
            case "PERSON_DI", "ROLE_PERSON_DI" -> UserRole.ROLE_PERSON_DI;
            case "HELPER", "ROLE_HELPER" -> UserRole.ROLE_HELPER;
            case "ADMINISTRATOR", "ROLE_ADMINISTRATOR", "ADMIN" -> UserRole.ROLE_ADMINISTRATOR;
            default -> throw new IllegalArgumentException(
                "Rôle invalide: " + role + ". Valeurs acceptées: PERSON_DI, HELPER, ADMINISTRATOR"
            );
        };
    }

    /**
     * Convertit un UserRole en string lisible.
     */
    private String convertRoleToString(UserRole role) {
        return switch (role) {
            case ROLE_PERSON_DI -> "PERSON_DI";
            case ROLE_HELPER -> "HELPER";
            case ROLE_ADMINISTRATOR -> "ADMINISTRATOR";
            default -> "UNKNOWN";
        };
    }

    /**
     * Convertit une TokenResponse gRPC en DTO.
     */
    private TokenResponseDto convertTokenResponse(TokenResponse grpcResponse) {
        return TokenResponseDto.builder()
            .accessToken(grpcResponse.getAccessToken())
            .refreshToken(grpcResponse.getRefreshToken())
            .tokenType(grpcResponse.getTokenType())
            .expiresIn(grpcResponse.getExpiresIn())
            .userId(grpcResponse.getUserId())
            .role(convertRoleToString(grpcResponse.getRole()))
            .username(grpcResponse.getUsername())
            .email(grpcResponse.getEmail())
            .build();
    }

    /**
     * Gère les erreurs gRPC et les convertit en réponses HTTP.
     */
    private ResponseEntity<?> handleGrpcError(StatusRuntimeException e) {
        Status status = e.getStatus();
        String description = status.getDescription() != null 
            ? status.getDescription() 
            : "Une erreur est survenue";
        
        HttpStatus httpStatus = switch (status.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        
        log.error("Erreur gRPC - code: {}, message: {}", status.getCode(), description);
        
        return ResponseEntity.status(httpStatus).body(Map.of(
            "error", httpStatus.getReasonPhrase(),
            "message", description,
            "grpcCode", status.getCode().name()
        ));
    }
}