package com.codistrib.authservice.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {

    private final StringRedisTemplate redis;

    public TokenBlacklistService(StringRedisTemplate redis) { this.redis = redis; }

    // Ajoute le token dans la liste noire
    public void blacklist(String jti, Duration ttl) {
        if (jti == null || ttl == null || ttl.isNegative() || ttl.isZero())
            return;
        try {
            redis.opsForValue().set("auth:blacklist:" + jti, "1", ttl);
        }
        catch (Exception e) { /* log.warn("Redis indisponible pour blacklist", e); */ }
    }

    // Vérifie si le token est dans la liste noire
    public boolean isBlacklisted(String jti) {
        try {
            return redis.hasKey("auth:blacklist:" + jti);
        }
        catch (Exception e) { /* log.warn("Redis indisponible pour check blacklist", e); */ return false; }
    }
}