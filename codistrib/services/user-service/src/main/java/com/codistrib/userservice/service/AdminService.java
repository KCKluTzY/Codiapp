package com.codistrib.userservice.service;

import com.codistrib.userservice.domain.model.Admin;
import com.codistrib.userservice.dto.AdminDtos;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    /**
     * Créer un nouvel Admin
     * Appelé par un SUPER_ADMIN
     */
    Admin createAdmin(AdminDtos.CreateAdminRequest request);

    /**
     * Récupérer un Admin par son user_id
     */
    Admin getAdminById(UUID userId);

    /**
     * Mettre à jour un Admin
     */
    Admin updateAdmin(AdminDtos.UpdateAdminRequest request);

    /**
     * Récupérer tous les Admins par niveau de rôle
     */
    List<Admin> getAdminsByRoleLevel(String roleLevel);

    /**
     * Récupérer tous les Super Admins
     */
    List<Admin> getSuperAdmins();

    /**
     * Vérifier si un utilisateur est Super Admin
     */
    boolean isSuperAdmin(UUID userId);

    /**
     * Vérifier si un Admin a une permission spécifique
     */
    AdminDtos.CheckPermissionResponse checkPermission(AdminDtos.CheckPermissionRequest request);

    /**
     * Récupérer les permissions disponibles dans le système
     */
    AdminDtos.AvailablePermissions getAvailablePermissions();

    /**
     * Récupérer le dashboard admin avec statistiques
     */
    AdminDtos.AdminDashboard getAdminDashboard();

    /**
     * Récupérer les statistiques d'un Admin
     */
    AdminDtos.AdminStats getAdminStats(UUID userId);
}