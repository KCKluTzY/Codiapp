package com.codistrib.apigateway.grpc;

import com.codistrib.proto.auth.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Client gRPC pour communiquer avec Auth Service.
 * 
 * Ce client :
 * - Gère la connexion gRPC vers auth-service
 * - Expose des méthodes pour chaque opération (login, register, etc.)
 * - Gère les erreurs et la fermeture propre du channel
 */
@Slf4j
@Component
public class AuthServiceGrpcClient {

    @Value("${grpc.client.auth-service.host:localhost}")
    private String host;

    @Value("${grpc.client.auth-service.port:9001}")
    private int port;

    private ManagedChannel channel;
    private AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    /**
     * Initialise la connexion gRPC au démarrage.
     */
    @PostConstruct
    public void init() {
        log.info("Initialisation du client gRPC Auth Service - {}:{}", host, port);
        
        channel = ManagedChannelBuilder
            .forAddress(host, port)
            .usePlaintext()  // Pas de TLS pour le dev (à changer en prod)
            .build();
        
        blockingStub = AuthServiceGrpc.newBlockingStub(channel);
        
        log.info("Client gRPC Auth Service initialisé");
    }

    /**
     * Ferme la connexion gRPC proprement à l'arrêt.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Fermeture du client gRPC Auth Service");
        
        if (channel != null && !channel.isShutdown()) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.warn("Interruption lors de la fermeture du channel gRPC", e);
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Inscription d'un nouvel utilisateur.
     */
    public TokenResponse register(RegisterRequest request) {
        log.debug("gRPC Register - username: {}, email: {}, role: {}", 
            request.getUsername(), request.getEmail(), request.getRole());
        
        try {
            TokenResponse response = blockingStub.register(request);
            log.debug("gRPC Register success - userId: {}", response.getUserId());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC Register failed - code: {}, message: {}", 
                e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Connexion d'un utilisateur.
     */
    public TokenResponse login(LoginRequest request) {
        log.debug("gRPC Login - identifier: {}", request.getIdentifier());
        
        try {
            TokenResponse response = blockingStub.login(request);
            log.debug("gRPC Login success - userId: {}", response.getUserId());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC Login failed - code: {}, message: {}", 
                e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Rafraîchissement du token.
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.debug("gRPC RefreshToken");
        
        try {
            TokenResponse response = blockingStub.refreshToken(request);
            log.debug("gRPC RefreshToken success - userId: {}", response.getUserId());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC RefreshToken failed - code: {}, message: {}", 
                e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Validation d'un token.
     */
    public TokenClaims validateToken(ValidateTokenRequest request) {
        log.debug("gRPC ValidateToken");
        
        try {
            TokenClaims response = blockingStub.validateToken(request);
            log.debug("gRPC ValidateToken - isValid: {}, userId: {}", 
                response.getIsValid(), response.getUserId());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC ValidateToken failed - code: {}, message: {}", 
                e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Déconnexion d'un utilisateur.
     */
    public LogoutResponse logout(LogoutRequest request) {
        log.debug("gRPC Logout");
        
        try {
            LogoutResponse response = blockingStub.logout(request);
            log.debug("gRPC Logout - success: {}", response.getSuccess());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC Logout failed - code: {}, message: {}", 
                e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }
}