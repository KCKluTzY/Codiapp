package com.codistrib.authservice.grpc;

import com.codistrib.authservice.domain.enums.UserRole;
import com.codistrib.authservice.service.impl.AuthDomainService;
import com.codistrib.authservice.dto.AuthDtos.*;
import com.codistrib.authservice.service.impl.AuthDomainService.TokenClaimsDto;
import com.codistrib.proto.auth.*; // généré par protoc
import io.grpc.Status;
import io.grpc.StatusException;
import jakarta.validation.ConstraintViolationException;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;

@GrpcService
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {
    private final AuthDomainService service;
    public AuthGrpcService(AuthDomainService service) { this.service = service; }

    @Override
    public void register(RegisterRequest req, StreamObserver<TokenResponse> res) {
        try {
            var tp = service.register(new RegisterCmd(req.getUsername(), req.getEmail(), req.getPassword(), mapRole(req.getRole())));
            res.onNext(toTokenResponse(tp));
            res.onCompleted();
        } catch (RuntimeException e) { res.onError(toStatus(e)); }
    }

    @Override
    public void login(LoginRequest req, StreamObserver<TokenResponse> res) {
        try {
            var tp = service.login(new LoginCmd(req.getIdentifier(), req.getPassword()));
            res.onNext(toTokenResponse(tp));
            res.onCompleted();
        } catch (RuntimeException e) { res.onError(toStatus(e)); }
    }

    @Override
    public void refreshToken(RefreshTokenRequest req, StreamObserver<TokenResponse> res) {
        try {
            var tp = service.refresh(req.getRefreshToken());
            res.onNext(toTokenResponse(tp));
            res.onCompleted();
        } catch (RuntimeException e) { res.onError(toStatus(e)); }
    }

    @Override
    public void validateToken(ValidateTokenRequest req, StreamObserver<TokenClaims> res) {
        TokenClaimsDto c = service.validate(req.getAccessToken());
        res.onNext(toClaims(c));
        res.onCompleted();
    }

    @Override
    public void logout(LogoutRequest req, StreamObserver<LogoutResponse> res) {
        service.logout(req.getAccessToken(), req.getRefreshToken());
        res.onNext(LogoutResponse.newBuilder().setSuccess(true).build());
        res.onCompleted();
    }

    private StatusException toStatus(RuntimeException e) {
        if (e instanceof ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a,b) -> a + "; " + b)
                    .orElse("Invalid arguments");
            return Status.INVALID_ARGUMENT.withDescription(msg).asException();
        }
        // vous pouvez affiner: Unauthorized -> UNAUTHENTICATED, Forbidden -> PERMISSION_DENIED, etc.
        return Status.INTERNAL.withDescription(e.getMessage()).asException();
    }

    private TokenResponse toTokenResponse(TokenPair tp) {
        return TokenResponse.newBuilder()
                .setAccessToken(tp.accessToken())
                .setRefreshToken(tp.refreshToken() == null ? "" : tp.refreshToken())
                .setTokenType(tp.tokenType())
                .setExpiresIn(tp.expiresIn())
                .setUserId(tp.userId())
                .setRole(toProtoRole(tp.role()))
                .setUsername(tp.username())
                .setEmail(tp.email())
                .build();
    }

    private TokenClaims toClaims(TokenClaimsDto c) {
        TokenClaims.Builder b = TokenClaims.newBuilder().setIsValid(c.isValid());

        if (c.isValid()) {
            b.setUserId(
                    c.userId()).
                    setUsername(c.username()).
                    setEmail(c.email()).
                    setRole(toProtoRole(UserRole.valueOf(c.role())));
        }
        return b.build();
    }

    private UserRole mapRole(com.codistrib.proto.auth.UserRole role) {
        return switch (role) {
            case ROLE_PERSON_DI -> UserRole.ROLE_PERSON_DI;
            case ROLE_HELPER -> UserRole.ROLE_HELPER;
            case ROLE_ADMINISTRATOR -> UserRole.ROLE_ADMINISTRATOR;
            default -> UserRole.ROLE_PERSON_DI;
        };
    }
    private com.codistrib.proto.auth.UserRole toProtoRole(UserRole role) {
        return switch (role) {
            case ROLE_PERSON_DI -> com.codistrib.proto.auth.UserRole.ROLE_PERSON_DI;
            case ROLE_HELPER -> com.codistrib.proto.auth.UserRole.ROLE_HELPER;
            case ROLE_ADMINISTRATOR -> com.codistrib.proto.auth.UserRole.ROLE_ADMINISTRATOR;
        };
    }
}