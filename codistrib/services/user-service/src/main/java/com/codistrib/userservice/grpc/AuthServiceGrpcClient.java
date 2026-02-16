package com.codistrib.userservice.grpc;

import com.codistrib.proto.auth.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

@Component
@Slf4j
public class AuthServiceGrpcClient {

    @Value("${grpc.client.auth-service.host}")
    private String authServiceHost;

    @Value("${grpc.client.auth-service.port}")
    private int authServicePort;

    private ManagedChannel channel;
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;

    @PostConstruct
    public void init() {
        log.info("Initialisation du client gRPC Auth Service: {}:{}", authServiceHost, authServicePort);
        
        channel = ManagedChannelBuilder
                .forAddress(authServiceHost, authServicePort)
                .usePlaintext()  // Pas de TLS pour le dev (à changer en prod)
                .build();
        
        authServiceStub = AuthServiceGrpc.newBlockingStub(channel);
        
        log.info("Client gRPC Auth Service initialisé avec succès");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            log.info("Fermeture du canal gRPC Auth Service");
            channel.shutdown();
        }
    }

    /**
     * Créer des credentials pour une PersonDI via l'endpoint Register
     */
    public UUID createCredentialsForPersonDI(UUID userId, String email, String password) {
        log.info("Appel gRPC Auth Service - Register: email={}, userId={}", email, userId);
        
        try {
            RegisterRequest request = RegisterRequest.newBuilder()
                    .setUsername(email)  // Utiliser email comme username
                    .setEmail(email)
                    .setPassword(password)
                    .setRole(UserRole.ROLE_PERSON_DI)
                    .build();
            
            TokenResponse response = authServiceStub.register(request);
            
            UUID authId = UUID.fromString(response.getUserId());
            log.info("Credentials créés avec succès via Auth Service: authId={}", authId);
            
            return authId;
            
        } catch (StatusRuntimeException e) {
            log.error("Erreur gRPC lors de la création de credentials: {}", e.getStatus());
            throw new RuntimeException("Erreur lors de la création des credentials dans Auth Service: " + 
                    e.getStatus().getDescription(), e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'appel gRPC: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la communication avec Auth Service", e);
        }
    }

    /**
     * Valider un token (utilitaire, pas utilisé pour l'instant)
     */
    public TokenClaims validateToken(String accessToken) {
        log.debug("Appel gRPC Auth Service - ValidateToken");
        
        try {
            ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                    .setAccessToken(accessToken)
                    .build();
            
            return authServiceStub.validateToken(request);
            
        } catch (StatusRuntimeException e) {
            log.error("Erreur gRPC lors de la validation du token: {}", e.getStatus());
            throw new RuntimeException("Erreur lors de la validation du token", e);
        }
    }
}