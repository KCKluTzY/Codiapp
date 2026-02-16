package com.codistrib.apigateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Premier filtre
@RequiredArgsConstructor
public class RateLimitFilter implements WebFilter {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate-limit.default.requests-per-second:10}")
    private int requestsPerSecond;

    @Value("${rate-limit.default.burst-capacity:20}")
    private int burstCapacity;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final Duration WINDOW_DURATION = Duration.ofSeconds(1);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!rateLimitEnabled) {
            return chain.filter(exchange);
        }

        String clientIp = getClientIp(exchange);
        String path = exchange.getRequest().getPath().value();
        int limit = getLimitForPath(path);
        String key = RATE_LIMIT_PREFIX + clientIp;
        
        log.debug("RateLimitFilter - IP: {}, Path: {}, Limite: {}/s", clientIp, path, limit);

        return redisTemplate.opsForValue()
            .increment(key)
            .flatMap(count -> {
                if (count == 1) {
                    return redisTemplate.expire(key, WINDOW_DURATION)
                        .thenReturn(count);
                }
                return Mono.just(count);
            })
            .flatMap(count -> {
                if (count > limit) {
                    log.warn("Rate limit dépassé - IP: {}, Count: {}/{}", clientIp, count, limit);
                    return onRateLimitExceeded(exchange, count, limit);
                }
                
                exchange.getResponse().getHeaders()
                    .add("X-RateLimit-Limit", String.valueOf(limit));
                exchange.getResponse().getHeaders()
                    .add("X-RateLimit-Remaining", String.valueOf(Math.max(0, limit - count)));
                
                return chain.filter(exchange);
            })
            .onErrorResume(e -> {
                log.error("Erreur Redis dans RateLimitFilter: {}", e.getMessage());
                return chain.filter(exchange);
            });
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null) {
            InetAddress address = remoteAddress.getAddress();
            if (address != null) {
                return address.getHostAddress();
            }
        }
        
        return "unknown";
    }

    private int getLimitForPath(String path) {
        if (path.contains("/auth/login")) {
            return 5;
        }
        if (path.contains("/auth/register")) {
            return 2;
        }
        return burstCapacity;
    }

    private Mono<Void> onRateLimitExceeded(ServerWebExchange exchange, Long count, int limit) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().add("Retry-After", "1");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String path = exchange.getRequest().getPath().value();
        
        String body = String.format("""
            {
                "timestamp": "%s",
                "status": 429,
                "error": "Too Many Requests",
                "message": "Limite de requêtes dépassée. Veuillez réessayer dans quelques secondes.",
                "path": "%s",
                "limit": %d,
                "current": %d
            }
            """, 
            timestamp, 
            path,
            limit,
            count
        );

        DataBuffer buffer = response.bufferFactory()
            .wrap(body.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
}