package com.codistrib.userservice.grpc;

import com.codistrib.proto.user.*;
import com.codistrib.userservice.domain.model.User;
import com.codistrib.userservice.dto.UserDtos;
import com.codistrib.userservice.exception.UserNotFoundException;
import com.codistrib.userservice.exception.ValidationException;
import com.codistrib.userservice.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("gRPC - CreateUser appelé: name={}, authId={}", 
                request.getName(), request.getAuthId());
        
        try {
            // Convertir gRPC request en DTO
            UserDtos.CreateUserRequest createRequest = UserDtos.CreateUserRequest.builder()
                    .authId(UUID.fromString(request.getAuthId()))
                    .name(request.getName())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            
            // Créer l'utilisateur
            User user = userService.createUser(createRequest);
            
            // Construire la réponse gRPC
            CreateUserResponse response = CreateUserResponse.newBuilder()
                    .setUserId(user.getUserId().toString())
                    .setAuthId(user.getAuthId().toString())
                    .setName(user.getName())
                    .setStatus(user.getStatus().name())
                    .setSuccess(true)
                    .setMessage("Utilisateur créé avec succès")
                    .build();
            
            log.info("gRPC - Utilisateur créé: userId={}", user.getUserId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (ValidationException e) {
            log.error("gRPC - Erreur de validation: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC - Erreur lors de la création: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Erreur interne: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        log.debug("gRPC - GetUser appelé: userId={}", request.getUserId());
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userService.getUserById(userId);
            
            GetUserResponse response = GetUserResponse.newBuilder()
                    .setUserId(user.getUserId().toString())
                    .setAuthId(user.getAuthId() != null ? user.getAuthId().toString() : "")
                    .setName(user.getName())
                    .setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "")
                    .setStatus(user.getStatus().name())
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.error("gRPC - Utilisateur non trouvé: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC - Erreur: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Erreur interne: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserByAuthId(GetUserByAuthIdRequest request, 
                                 StreamObserver<GetUserResponse> responseObserver) {
        log.debug("gRPC - GetUserByAuthId appelé: authId={}", request.getAuthId());
        
        try {
            UUID authId = UUID.fromString(request.getAuthId());
            User user = userService.getUserByAuthId(authId);
            
            GetUserResponse response = GetUserResponse.newBuilder()
                    .setUserId(user.getUserId().toString())
                    .setAuthId(user.getAuthId().toString())
                    .setName(user.getName())
                    .setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "")
                    .setStatus(user.getStatus().name())
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.error("gRPC - Utilisateur non trouvé: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC - Erreur: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Erreur interne: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateAuthId(UpdateAuthIdRequest request, 
                             StreamObserver<UpdateAuthIdResponse> responseObserver) {
        log.info("gRPC - UpdateAuthId appelé: userId={}, authId={}", 
                request.getUserId(), request.getAuthId());
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            UUID authId = UUID.fromString(request.getAuthId());
            
            User user = userService.updateAuthId(userId, authId);
            
            UpdateAuthIdResponse response = UpdateAuthIdResponse.newBuilder()
                    .setUserId(user.getUserId().toString())
                    .setAuthId(user.getAuthId().toString())
                    .setStatus(user.getStatus().name())
                    .setSuccess(true)
                    .setMessage("AuthId mis à jour avec succès")
                    .build();
            
            log.info("gRPC - AuthId mis à jour: userId={}", userId);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.error("gRPC - Utilisateur non trouvé: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (ValidationException e) {
            log.error("gRPC - Erreur de validation: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC - Erreur: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Erreur interne: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void validateUser(ValidateUserRequest request, 
                            StreamObserver<ValidateUserResponse> responseObserver) {
        log.debug("gRPC - ValidateUser appelé: userId={}", request.getUserId());
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            UserDtos.ValidateUserResponse validation = userService.validateUser(userId);
            
            ValidateUserResponse response = ValidateUserResponse.newBuilder()
                    .setValid(validation.isValid())
                    .setUserId(validation.getUserId().toString())
                    .setName(validation.getName() != null ? validation.getName() : "")
                    .setStatus(validation.getStatus() != null ? validation.getStatus().name() : "")
                    .setMessage(validation.getMessage())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC - Erreur: {}", e.getMessage(), e);
            
            // En cas d'erreur, retourner valid=false
            ValidateUserResponse response = ValidateUserResponse.newBuilder()
                    .setValid(false)
                    .setUserId(request.getUserId())
                    .setMessage("Erreur lors de la validation: " + e.getMessage())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserSnapshot(GetUserSnapshotRequest request, 
                               StreamObserver<UserSnapshotResponse> responseObserver) {
        log.debug("gRPC - GetUserSnapshot appelé: userId={}", request.getUserId());
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            UserDtos.UserSnapshot snapshot = userService.getUserSnapshot(userId);
            
            UserSnapshotResponse response = UserSnapshotResponse.newBuilder()
                    .setUserId(snapshot.getUserId().toString())
                    .setName(snapshot.getName())
                    .setProfilePictureUrl(snapshot.getProfilePictureUrl() != null ? 
                            snapshot.getProfilePictureUrl() : "")
                    .setRole(snapshot.getRole())
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.error("gRPC - Utilisateur non trouvé: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC - Erreur: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Erreur interne: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void checkUserExists(CheckUserExistsRequest request, 
                               StreamObserver<CheckUserExistsResponse> responseObserver) {
        log.debug("gRPC - CheckUserExists appelé: userId={}", request.getUserId());
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            boolean exists = userService.userExists(userId);
            
            CheckUserExistsResponse response = CheckUserExistsResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setExists(exists)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC - Erreur: {}", e.getMessage(), e);
            
            CheckUserExistsResponse response = CheckUserExistsResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setExists(false)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}