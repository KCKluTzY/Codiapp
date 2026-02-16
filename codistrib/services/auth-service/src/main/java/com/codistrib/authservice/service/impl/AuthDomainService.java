package com.codistrib.authservice.service.impl;

import com.codistrib.authservice.domain.enums.UserRole;
import com.codistrib.authservice.domain.model.RefreshToken;
import com.codistrib.authservice.domain.model.UserAuth;
import com.codistrib.authservice.domain.repository.RefreshTokenRepository;
import com.codistrib.authservice.domain.repository.UserAuthRepository;
import com.codistrib.authservice.dto.AuthDtos.*;
import com.codistrib.authservice.exception.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AuthDomainService {

    private final Validator validator; // AJOUT


    private final UserAuthRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final TokenBlacklistService blacklist;

    @Value("${jwt.refresh-token-validity-seconds}") private long refreshValidity;
    @Value("${jwt.access-token-validity-seconds}") private long accessValidity;

    public AuthDomainService(Validator validator, UserAuthRepository users, RefreshTokenRepository refreshTokens,
                             PasswordEncoder encoder, JwtService jwt, TokenBlacklistService blacklist) {

        this.users = users; this.refreshTokens = refreshTokens; this.encoder = encoder; this.jwt = jwt; this.blacklist = blacklist; this.validator = validator;
    }

    private <T> void assertValid(T obj) {
        Set<ConstraintViolation<T>> v = validator.validate(obj);
        if (!v.isEmpty()) throw new ConstraintViolationException(v);
    }

    public TokenPair register(RegisterCmd cmd) {
        assertValid(cmd);

        users.findByEmailIgnoreCase(cmd.email()).ifPresent(u -> { throw new ConflictException("email already used");});
        users.findByUsernameIgnoreCase(cmd.username()).ifPresent(u -> { throw new ConflictException("username already used");});

        UserAuth user = new UserAuth();
        user.setEmail(cmd.email());
        user.setUsername(cmd.username());
        user.setPasswordHash(encoder.encode(cmd.password()));
        user.setRole(cmd.role() == null ? UserRole.ROLE_PERSON_DI : cmd.role());

        users.save(user);
        return issueTokensFor(user, true);
    }

    public TokenPair login(LoginCmd cmd) {
        UserAuth user = users.findByEmailIgnoreCase(cmd.identifier())
                .or(() -> users.findByUsernameIgnoreCase(cmd.identifier()))
                .orElseThrow(() -> new UnauthorizedException("invalid credentials"));

        if (!user.isActive() || user.isLocked())
            throw new ForbiddenException("account disabled or locked");

        if (!encoder.matches(cmd.password(), user.getPasswordHash())) {
            // handleFailedAttempt(user);
            throw new UnauthorizedException("invalid credentials");
        }

        resetAttempts(user);
        user.setLastLoginAt(Instant.now());
        return issueTokensFor(user, true);
    }

    public TokenPair refresh(String refreshToken) {
        RefreshToken rt = refreshTokens.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("invalid refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now()))
            throw new UnauthorizedException("refresh token expired or revoked");

        UserAuth user = users.findById(rt.getUserId()).orElseThrow(() -> new NotFoundException("user not found"));
        rt.setRevoked(true); // rotation le token

        refreshTokens.save(rt);
        return issueTokensFor(user, true);
    }

    public void logout(String accessToken, String refreshToken) {
        Jws<Claims> jws = jwt.parse(accessToken);
        String jti = jws.getBody().getId();
        Instant exp = jws.getBody().getExpiration().toInstant();

        blacklist.blacklist(jti, Duration.between(Instant.now(), exp));
        refreshTokens.findByToken(refreshToken).ifPresent(rt -> { rt.setRevoked(true); refreshTokens.save(rt); });
    }

    public TokenClaimsDto validate(String accessToken) {
        try {
            Jws<Claims> jws = jwt.parse(accessToken);
            Claims c = jws.getBody();

            if (blacklist.isBlacklisted(c.getId()))
                return TokenClaimsDto.invalid();

            return TokenClaimsDto.valid(
                    c.getSubject(),
                    (String) c.get("username"),
                    (String) c.get("email"),
                    (String) c.get("role")
            );
        } catch (Exception ex) {
            return TokenClaimsDto.invalid();
        }
    }

    private TokenPair issueTokensFor(UserAuth user, boolean withRefresh) {
        String access = jwt.generateAccessToken(user);
        String refresh = null;

        if (withRefresh) {
            refresh = UUID.randomUUID() + "." + UUID.randomUUID();
            RefreshToken rt = new RefreshToken();
            rt.setUserId(user.getId());
            rt.setToken(refresh);
            rt.setExpiresAt(Instant.now().plusSeconds(refreshValidity));
            refreshTokens.save(rt);
        }

        return new TokenPair(
                access,
                refresh,
                "Bearer",
                accessValidity,
                user.getId().toString(),
                user.getRole(),
                user.getUsername(),
                user.getEmail());

    }


    /* // Si l'utilisateur échoue 5x alors block user
    private void handleFailedAttempt(UserAuth user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);

        if (user.getFailedAttempts() >= 5) user.setLocked(true);
        users.save(user);
    }*/

    private void resetAttempts(UserAuth user) {
        user.setFailedAttempts(0);
        user.setLocked(false);
        users.save(user);
    }

    // DTO interne pour validate
    public record TokenClaimsDto(String userId, String username, String email, String role, boolean isValid) {
        public static TokenClaimsDto valid(String userId, String username, String email, String role) {
            return new TokenClaimsDto(
                    userId,
                    username,
                    email,
                    role,
                    true);
        }
        public static TokenClaimsDto invalid() {
            return new TokenClaimsDto(
                    null,
                    null,
                    null,
                    null,
                    false); }
    }
}