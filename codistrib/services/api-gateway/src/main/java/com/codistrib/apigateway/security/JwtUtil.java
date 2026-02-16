package com.codistrib.apigateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    /**
     * Initialise la clé de signature après injection des propriétés.
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        log.info("JwtUtil initialisé avec succès");
    }

    /**
     * Valide un token JWT.
     * 
     * Vérifie :
     * 1. La signature (le token n'a pas été modifié)
     * 2. L'expiration (le token n'est pas expiré)
     * 3. Le format (le token est bien formé)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expiré");
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformé");
        } catch (SecurityException e) {
            log.warn("Signature JWT invalide");
        } catch (IllegalArgumentException e) {
            log.warn("Token JWT vide ou null");
        } catch (Exception e) {
            log.warn("Erreur de validation JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrait tous les claims du token.
     * 
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Extrait l'ID utilisateur du token.
     * 
     * Le user_id est stocké dans le "subject" du JWT.
     */
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrait le rôle de l'utilisateur du token.
     * 
     * Le rôle est stocké dans un claim personnalisé "role".
     * Valeurs possibles : PERSON_DI, HELPER, ADMINISTRATOR
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Extrait l'email de l'utilisateur du token.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    /**
     * Extrait le nom d'utilisateur du token.
     * 
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    /**
     * Vérifie si le token est expiré.
     * 
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}