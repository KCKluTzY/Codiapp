package com.codistrib.userservice.service.impl;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.enums.UserStatus;
import com.codistrib.userservice.domain.enums.ValidationStatus;
import com.codistrib.userservice.domain.model.Admin;
import com.codistrib.userservice.domain.model.User;
import com.codistrib.userservice.domain.repository.AdminRepository;
import com.codistrib.userservice.dto.AdminDtos;
import com.codistrib.userservice.dto.CredentialRequestDtos;
import com.codistrib.userservice.dto.HelperDtos;
import com.codistrib.userservice.exception.UnauthorizedException;
import com.codistrib.userservice.exception.UserNotFoundException;
import com.codistrib.userservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.codistrib.userservice.service.AdminService;
import com.codistrib.userservice.service.CredentialRequestService;
import com.codistrib.userservice.service.HelperService;
import com.codistrib.userservice.service.PersonDIService;
import com.codistrib.userservice.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;
    private final HelperService helperService;
    private final PersonDIService personDIService;
    private final CredentialRequestService credentialRequestService;

    // Permissions disponibles dans le système
    private static final List<String> AVAILABLE_PERMISSIONS = Arrays.asList(
            "VALIDATE_HELPERS",
            "VALIDATE_CREDENTIALS",
            "MANAGE_USERS",
            "VIEW_ANALYTICS",
            "MANAGE_ADMINS",
            "DELETE_USERS"
    );

    @Override
    public Admin createAdmin(AdminDtos.CreateAdminRequest request) {
        log.info("Création d'un nouvel Admin: {}", request.getName());
        
        // Vérifier que le créateur est un SUPER_ADMIN
        if (request.getCreatedBy() != null && !isSuperAdmin(request.getCreatedBy())) {
            throw new UnauthorizedException("Seul un SUPER_ADMIN peut créer d'autres admins");
        }
        
        // Valider les permissions
        validatePermissions(request.getPermissions());
        
        // Créer l'utilisateur User
        User user = User.builder()
                .authId(request.getAuthId())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .status(UserStatus.ACTIVE)
                .createdBy(request.getCreatedBy())
                .build();
        
        // Créer l'entité Admin
        Admin admin = Admin.builder()
                .user(user)
                .organizationId(request.getOrganizationId())
                .permissions(request.getPermissions())
                .roleLevel(request.getRoleLevel())
                .build();
        
        Admin savedAdmin = adminRepository.save(admin);
        log.info("Admin créé avec succès: userId={}, roleLevel={}", 
                savedAdmin.getUserId(), savedAdmin.getRoleLevel());
        
        return savedAdmin;
    }

    @Override
    @Transactional(readOnly = true)
    public Admin getAdminById(UUID userId) {
        log.debug("Récupération de l'Admin: userId={}", userId);
        return adminRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Admin avec l'ID " + userId + " non trouvé"));
    }

    @Override
    public Admin updateAdmin(AdminDtos.UpdateAdminRequest request) {
        log.info("Mise à jour de l'Admin: userId={}", request.getUserId());
        
        Admin admin = getAdminById(request.getUserId());
        User user = admin.getUser();
        
        // Mettre à jour User
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        // Mettre à jour Admin
        if (request.getOrganizationId() != null) {
            admin.setOrganizationId(request.getOrganizationId());
        }
        if (request.getPermissions() != null) {
            validatePermissions(request.getPermissions());
            admin.setPermissions(request.getPermissions());
        }
        if (request.getRoleLevel() != null) {
            admin.setRoleLevel(request.getRoleLevel());
        }
        
        Admin updatedAdmin = adminRepository.save(admin);
        log.info("Admin mis à jour avec succès: userId={}", updatedAdmin.getUserId());
        
        return updatedAdmin;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAdminsByRoleLevel(String roleLevel) {
        log.debug("Récupération des Admins par niveau de rôle: {}", roleLevel);
        return adminRepository.findByRoleLevel(roleLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getSuperAdmins() {
        log.debug("Récupération de tous les Super Admins");
        return adminRepository.findSuperAdmins();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSuperAdmin(UUID userId) {
        return adminRepository.isSuperAdmin(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDtos.CheckPermissionResponse checkPermission(AdminDtos.CheckPermissionRequest request) {
        log.debug("Vérification de permission: adminId={}, permission={}", 
                request.getAdminId(), request.getPermission());
        
        Admin admin = getAdminById(request.getAdminId());
        
        boolean hasPermission = adminRepository.hasPermission(
                request.getAdminId(), 
                request.getPermission()
        );
        
        String message = hasPermission ? 
                "L'admin possède cette permission" :
                "L'admin ne possède pas cette permission";
        
        return AdminDtos.CheckPermissionResponse.builder()
                .adminId(request.getAdminId())
                .permission(request.getPermission())
                .hasPermission(hasPermission)
                .roleLevel(admin.getRoleLevel())
                .message(message)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDtos.AvailablePermissions getAvailablePermissions() {
        log.debug("Récupération des permissions disponibles");
        
        List<AdminDtos.PermissionInfo> permissions = AVAILABLE_PERMISSIONS.stream()
                .map(permission -> createPermissionInfo(permission))
                .collect(Collectors.toList());
        
        return AdminDtos.AvailablePermissions.builder()
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDtos.AdminDashboard getAdminDashboard() {
        log.debug("Génération du dashboard admin");
        
        // Statistiques globales
        int totalUsers = (int) userService.countByStatus(UserStatus.ACTIVE) + 
                         (int) userService.countByStatus(UserStatus.PENDING_CREDENTIALS);
        
        // En attente de validation
        int helpersPending = (int) helperService.countByValidationStatus(ValidationStatus.PENDING);
        CredentialRequestDtos.PendingCredentialRequestsResponse pendingCreds = 
                credentialRequestService.getPendingRequests();
        
        // Récupérer les listes
        List<HelperDtos.HelperSummary> recentHelpersPending = helperService
                .getHelpersByValidationStatus(ValidationStatus.PENDING)
                .stream()
                .limit(5)
                .map(h -> toHelperSummary(h))
                .collect(Collectors.toList());
        
        List<CredentialRequestDtos.CredentialRequestSummary> recentCredsPending = 
                pendingCreds.getRequests().stream()
                .limit(5)
                .collect(Collectors.toList());
        
        return AdminDtos.AdminDashboard.builder()
                .totalUsers(totalUsers)
                .helpersPending(helpersPending)
                .credentialRequestsPending(pendingCreds.getTotalPending())
                .recentHelpersPending(recentHelpersPending)
                .recentCredentialRequestsPending(recentCredsPending)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDtos.AdminStats getAdminStats(UUID userId) {
        log.debug("Récupération des statistiques de l'Admin: userId={}", userId);
        
        Admin admin = getAdminById(userId);
        
        // Compter les Helpers validés (approuvés) par cet admin
        long helpersValidatedCount = helperService.getHelpersByValidationStatus(ValidationStatus.APPROVED)
                .stream()
                .filter(h -> h.getValidatedBy() != null && h.getValidatedBy().equals(userId))
                .count();
        
        // Compter les Helpers rejetés par cet admin
        long helpersRejectedCount = helperService.getHelpersByValidationStatus(ValidationStatus.REJECTED)
                .stream()
                .filter(h -> h.getValidatedBy() != null && h.getValidatedBy().equals(userId))
                .count();
        
        // Compter les credentials approuvés par cet admin
        long credentialsApprovedCount = credentialRequestService
                .getCredentialRequestsByStatus(CredentialRequestStatus.APPROVED)
                .stream()
                .filter(cr -> cr.getReviewedBy() != null && cr.getReviewedBy().equals(userId))
                .count();
        
        // Compter les credentials rejetés par cet admin
        long credentialsRejectedCount = credentialRequestService
                .getCredentialRequestsByStatus(CredentialRequestStatus.REJECTED)
                .stream()
                .filter(cr -> cr.getReviewedBy() != null && cr.getReviewedBy().equals(userId))
                .count();
        
        // Déterminer la dernière action (dernière validation ou rejet)
        // On prend le max entre les dates de validation des Helpers et des CredentialRequests
        java.time.LocalDateTime lastActionAt = null;
        
        // Dernière validation Helper
        java.time.LocalDateTime lastHelperValidation = helperService
                .getHelpersByValidationStatus(ValidationStatus.APPROVED)
                .stream()
                .filter(h -> h.getValidatedBy() != null && h.getValidatedBy().equals(userId))
                .map(h -> h.getValidatedAt())
                .filter(date -> date != null)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
        
        // Dernière validation Credential
        java.time.LocalDateTime lastCredentialValidation = credentialRequestService
                .getCredentialRequestsByStatus(CredentialRequestStatus.APPROVED)
                .stream()
                .filter(cr -> cr.getReviewedBy() != null && cr.getReviewedBy().equals(userId))
                .map(cr -> cr.getReviewedAt())
                .filter(date -> date != null)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
        
        // Prendre la plus récente des deux
        if (lastHelperValidation != null && lastCredentialValidation != null) {
            lastActionAt = lastHelperValidation.isAfter(lastCredentialValidation) ? 
                    lastHelperValidation : lastCredentialValidation;
        } else if (lastHelperValidation != null) {
            lastActionAt = lastHelperValidation;
        } else if (lastCredentialValidation != null) {
            lastActionAt = lastCredentialValidation;
        }
        
        log.info("Statistiques Admin calculées: userId={}, helpersValidated={}, credentialsApproved={}", 
                userId, helpersValidatedCount, credentialsApprovedCount);
        
        return AdminDtos.AdminStats.builder()
                .adminId(userId)
                .adminName(admin.getUser().getName())
                .roleLevel(admin.getRoleLevel())
                .helpersValidated((int) helpersValidatedCount)
                .helpersRejected((int) helpersRejectedCount)
                .credentialsApproved((int) credentialsApprovedCount)
                .credentialsRejected((int) credentialsRejectedCount)
                .lastActionAt(lastActionAt)
                .build();
    }

    /**
     * Valider que les permissions existent
     */
    private void validatePermissions(List<String> permissions) {
        for (String permission : permissions) {
            if (!AVAILABLE_PERMISSIONS.contains(permission)) {
                throw new ValidationException("permission", 
                        "Permission invalide: " + permission);
            }
        }
    }

    /**
     * Créer une PermissionInfo
     */
    private AdminDtos.PermissionInfo createPermissionInfo(String code) {
        String displayName;
        String description;
        String category;
        
        switch (code) {
            case "VALIDATE_HELPERS":
                displayName = "Valider les aidants";
                description = "Approuver ou rejeter les inscriptions de Helpers";
                category = "HELPER_MANAGEMENT";
                break;
            case "VALIDATE_CREDENTIALS":
                displayName = "Valider les credentials";
                description = "Approuver ou rejeter les demandes de credentials pour PersonDI";
                category = "CREDENTIAL_MANAGEMENT";
                break;
            case "MANAGE_USERS":
                displayName = "Gérer les utilisateurs";
                description = "Créer, modifier, supprimer des utilisateurs";
                category = "USER_MANAGEMENT";
                break;
            case "VIEW_ANALYTICS":
                displayName = "Voir les analyses";
                description = "Accéder aux statistiques et rapports";
                category = "ANALYTICS";
                break;
            case "MANAGE_ADMINS":
                displayName = "Gérer les admins";
                description = "Créer et modifier d'autres administrateurs";
                category = "ADMIN_MANAGEMENT";
                break;
            case "DELETE_USERS":
                displayName = "Supprimer des utilisateurs";
                description = "Supprimer définitivement des utilisateurs";
                category = "USER_MANAGEMENT";
                break;
            default:
                displayName = code;
                description = "Permission " + code;
                category = "OTHER";
        }
        
        return AdminDtos.PermissionInfo.builder()
                .code(code)
                .displayName(displayName)
                .description(description)
                .category(category)
                .build();
    }

    /**
     * Convertir Helper en HelperSummary (méthode utilitaire)
     */
    private HelperDtos.HelperSummary toHelperSummary(com.codistrib.userservice.domain.model.Helper helper) {
        long assignedCount = personDIService.countByAssignedHelper(helper.getUserId());
        
        return HelperDtos.HelperSummary.builder()
                .userId(helper.getUserId())
                .name(helper.getUser().getName())
                .profilePictureUrl(helper.getUser().getProfilePictureUrl())
                .specializations(helper.getSpecializations())
                .availabilityStatus(helper.getAvailabilityStatus())
                .validationStatus(helper.getValidationStatus())
                .rating(helper.getRating())
                .totalAssists(helper.getTotalAssists())
                .currentAssignedPersons((int) assignedCount)
                .canTakeMore(assignedCount < helper.getMaxConcurrentAssists())
                .build();
    }
}