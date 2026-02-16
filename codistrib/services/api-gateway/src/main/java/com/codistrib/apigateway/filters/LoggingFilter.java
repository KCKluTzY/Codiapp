package com.codistrib.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Filtre de logging pour tracer toutes les requêtes.
 * Ordre : LOWEST_PRECEDENCE (s'exécute en dernier, après tous les autres filtres)
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE) // Dernier filtre
public class LoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String clientIp = getClientIp(request);
        String requestId = request.getId();
        
        Instant startTime = Instant.now();
        
        log.info("[{}] ▶ {} {} - IP: {}", requestId, method, path, clientIp);

        return chain.filter(exchange)
            .doFinally(signalType -> {
                ServerHttpResponse response = exchange.getResponse();
                Duration duration = Duration.between(startTime, Instant.now());
                int statusCode = response.getStatusCode() != null 
                    ? response.getStatusCode().value() 
                    : 0;
                
                String statusEmoji = getStatusEmoji(statusCode);
                
                log.info("[{}] {} {} {} - {}ms - {}", 
                    requestId, 
                    statusEmoji, 
                    method, 
                    path, 
                    duration.toMillis(),
                    statusCode
                );
            });
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        if (request.getRemoteAddress() != null && 
            request.getRemoteAddress().getAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        
        return "unknown";
    }

    private String getStatusEmoji(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "✅";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "↪️";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "⚠️";
        } else if (statusCode >= 500) {
            return "❌";
        }
        return "❓";
    }
}