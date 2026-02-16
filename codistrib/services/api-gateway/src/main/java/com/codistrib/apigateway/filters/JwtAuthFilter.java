package com.codistrib.apigateway.filters;

import com.codistrib.apigateway.security.JwtUtil;
import com.codistrib.apigateway.security.RouteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Après RateLimitFilter
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    @Value("${jwt.header}")
    private String authHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        log.debug("JwtAuthFilter - {} {}", method, path);

        // ÉTAPE 1 : Route publique ?
        if (routeValidator.isPublicRoute(path)) {
            log.debug("Route publique, pas d'authentification requise: {}", path);
            return chain.filter(exchange);
        }

        // ÉTAPE 2 : Header Authorization présent ?
        if (!request.getHeaders().containsKey(authHeader)) {
            log.warn("Header Authorization manquant pour: {}", path);
            return onError(exchange, "Token d'authentification manquant", HttpStatus.UNAUTHORIZED);
        }

        String authHeaderValue = request.getHeaders().getFirst(authHeader);
        
        if (authHeaderValue == null || !authHeaderValue.startsWith(tokenPrefix)) {
            log.warn("Format du token invalide pour: {}", path);
            return onError(exchange, "Format du token invalide. Utilisez: Bearer <token>", HttpStatus.UNAUTHORIZED);
        }

        // ÉTAPE 3 : Extraire et valider le token
        String token = authHeaderValue.substring(tokenPrefix.length());

        if (!jwtUtil.validateToken(token)) {
            log.warn("Token JWT invalide ou expiré pour: {}", path);
            return onError(exchange, "Token invalide ou expiré", HttpStatus.UNAUTHORIZED);
        }

        // ÉTAPE 4 : Extraire les informations du token
        String userId;
        String role;
        String email;
        
        try {
            userId = jwtUtil.extractUserId(token);
            role = jwtUtil.extractRole(token).substring(5);
            email = jwtUtil.extractEmail(token);
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction des claims JWT: {}", e.getMessage());
            return onError(exchange, "Token malformé", HttpStatus.UNAUTHORIZED);
        }

        if (userId == null || role == null) {
            log.warn("Claims obligatoires manquants dans le token");
            return onError(exchange, "Token incomplet", HttpStatus.UNAUTHORIZED);
        }

        log.debug("Token valide - userId: {}, role: {}", userId, role);

        // ÉTAPE 5 : Vérifier l'autorisation par rôle
        if (!routeValidator.canAccess(path, role)) {
            log.warn("Accès refusé - userId: {}, role: {}, path: {}", userId, role, path);
            return onError(exchange, 
                "Accès refusé. Rôle requis: " + routeValidator.getAllowedRoles(path), 
                HttpStatus.FORBIDDEN);
        }

        // ÉTAPE 6 : Ajouter les infos utilisateur aux headers
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", userId)
            .header("X-User-Role", role)
            .header("X-User-Email", email != null ? email : "")
            .build();

        log.debug("Requête autorisée - {} {} - userId: {}", method, path, userId);
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * Génère une réponse d'erreur JSON.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String path = exchange.getRequest().getPath().value();
        
        String body = String.format("""
            {
                "timestamp": "%s",
                "status": %d,
                "error": "%s",
                "message": "%s",
                "path": "%s"
            }
            """, 
            timestamp, 
            status.value(), 
            status.getReasonPhrase(), 
            message,
            path
        );

        DataBuffer buffer = response.bufferFactory()
            .wrap(body.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
}