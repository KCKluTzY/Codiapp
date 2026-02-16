package com.codistrib.userservice.service.impl;

import com.codistrib.userservice.domain.enums.UserStatus;
import com.codistrib.userservice.domain.model.User;
import com.codistrib.userservice.domain.repository.AdminRepository;
import com.codistrib.userservice.domain.repository.HelperRepository;
import com.codistrib.userservice.domain.repository.PersonDIRepository;
import com.codistrib.userservice.domain.repository.UserRepository;
import com.codistrib.userservice.dto.UserDtos;
import com.codistrib.userservice.exception.UserNotFoundException;
import com.codistrib.userservice.exception.ValidationException;
import com.codistrib.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PersonDIRepository personDIRepository;
    private final HelperRepository helperRepository;
    private final AdminRepository adminRepository;

    @Override
    public User createUser(UserDtos.CreateUserRequest request) {
        log.info("Création d'un nouvel utilisateur: {}", request.getName());
        
        // Vérifier que l'auth_id n'existe pas déjà
        if (request.getAuthId() != null && userRepository.existsByAuthId(request.getAuthId())) {
            throw new ValidationException("authId", "Un utilisateur avec cet auth_id existe déjà");
        }
        
        // Créer l'entité User
        User user = User.builder()
                .authId(request.getAuthId())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .profilePictureUrl(request.getProfilePictureUrl())
                .status(request.getAuthId() != null ? UserStatus.ACTIVE : UserStatus.PENDING_CREDENTIALS)
                .createdBy(request.getCreatedBy())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: userId={}", savedUser.getUserId());
        
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        log.debug("Récupération de l'utilisateur: userId={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByAuthId(UUID authId) {
        log.debug("Récupération de l'utilisateur par authId: {}", authId);
        return userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur avec authId " + authId + " non trouvé"));
    }

    @Override
    public User updateUser(UserDtos.UpdateUserRequest request) {
        log.info("Mise à jour de l'utilisateur: userId={}", request.getUserId());
        
        User user = getUserById(request.getUserId());
        
        // Mettre à jour les champs si fournis
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Utilisateur mis à jour avec succès: userId={}", updatedUser.getUserId());
        
        return updatedUser;
    }

    @Override
    public User updateUserStatus(UUID userId, UserStatus status) {
        log.info("Mise à jour du statut de l'utilisateur: userId={}, status={}", userId, status);
        
        User user = getUserById(userId);
        user.setStatus(status);
        
        return userRepository.save(user);
    }

    @Override
    public User updateAuthId(UUID userId, UUID authId) {
        log.info("Mise à jour de l'authId: userId={}, authId={}", userId, authId);
        
        // Vérifier que l'auth_id n'est pas déjà utilisé
        if (userRepository.existsByAuthId(authId)) {
            throw new ValidationException("authId", "Cet auth_id est déjà utilisé par un autre utilisateur");
        }
        
        User user = getUserById(userId);
        user.setAuthId(authId);
        user.setStatus(UserStatus.ACTIVE);  // Activer l'utilisateur
        
        User updatedUser = userRepository.save(user);
        log.info("AuthId mis à jour avec succès: userId={}", updatedUser.getUserId());
        
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByStatus(UserStatus status) {
        log.debug("Récupération des utilisateurs par statut: {}", status);
        return userRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersCreatedBy(UUID createdBy) {
        log.debug("Récupération des utilisateurs créés par: {}", createdBy);
        return userRepository.findByCreatedBy(createdBy);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authIdExists(UUID authId) {
        return userRepository.existsByAuthId(authId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.ValidateUserResponse validateUser(UUID userId) {
        log.debug("Validation de l'utilisateur: userId={}", userId);
        
        return userRepository.findById(userId)
                .map(user -> UserDtos.ValidateUserResponse.builder()
                        .valid(true)
                        .userId(user.getUserId())
                        .name(user.getName())
                        .status(user.getStatus())
                        .message("Utilisateur valide")
                        .build())
                .orElse(UserDtos.ValidateUserResponse.builder()
                        .valid(false)
                        .userId(userId)
                        .message("Utilisateur non trouvé")
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserSnapshot getUserSnapshot(UUID userId) {
        log.debug("Récupération du snapshot utilisateur: userId={}", userId);
        
        User user = getUserById(userId);
        
        // Déterminer le rôle réel en vérifiant les tables spécialisées
        String role = determineUserRole(userId);
        
        return UserDtos.UserSnapshot.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(role)
                .build();
    }

    /**
     * Déterminer le rôle d'un utilisateur en vérifiant les tables spécialisées
     */
    private String determineUserRole(UUID userId) {
        // Vérifier dans l'ordre de priorité
        if (personDIRepository.existsById(userId)) {
            log.debug("Utilisateur {} identifié comme PERSONNE_DI", userId);
            return "PERSONNE_DI";
        }
        
        if (helperRepository.existsById(userId)) {
            log.debug("Utilisateur {} identifié comme HELPER", userId);
            return "HELPER";
        }
        
        if (adminRepository.existsById(userId)) {
            log.debug("Utilisateur {} identifié comme ADMIN", userId);
            return "ADMIN";
        }
        
        // Cas rare : User existe mais pas dans les tables spécialisées
        log.warn("Utilisateur {} n'a pas de rôle spécialisé", userId);
        return "UNKNOWN";
    }

    @Override
    public void deleteUser(UUID userId) {
        log.info("Suppression (soft delete) de l'utilisateur: userId={}", userId);
        
        User user = getUserById(userId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        
        log.info("Utilisateur désactivé avec succès: userId={}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }
}