package com.codistrib.userservice.domain.repository;

import com.codistrib.userservice.domain.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    /**
     * Rechercher un Admin par user_id
     */
    Optional<Admin> findByUserId(UUID userId);

    /**
     * Rechercher les Admins par niveau de rôle
     */
    List<Admin> findByRoleLevel(String roleLevel);

    /**
     * Rechercher les Admins d'une organisation spécifique
     */
    List<Admin> findByOrganizationId(UUID organizationId);

    /**
     * Rechercher les Super Admins (pas d'organisation)
     */
    @Query("SELECT a FROM Admin a WHERE a.roleLevel = 'SUPER_ADMIN'")
    List<Admin> findSuperAdmins();

    /**
     * Vérifier si un utilisateur est Super Admin
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Admin a WHERE a.userId = :userId AND a.roleLevel = 'SUPER_ADMIN'")
    boolean isSuperAdmin(@Param("userId") UUID userId);

    /**
     * Rechercher les Admins ayant une permission spécifique
     * Utilise une requête SQL native car JPQL ne supporte pas bien ANY() avec arrays
     */
    @Query(value = "SELECT * FROM admins WHERE :permission = ANY(permissions)", nativeQuery = true)
    List<Admin> findByPermission(@Param("permission") String permission);

    /**
     * Vérifier si un Admin a une permission spécifique
     * Utilise une requête SQL native car JPQL ne supporte pas bien ANY() avec arrays
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
                   "FROM admins WHERE user_id = :userId " +
                   "AND (:permission = ANY(permissions) OR role_level = 'SUPER_ADMIN')", 
           nativeQuery = true)
    boolean hasPermission(@Param("userId") UUID userId, @Param("permission") String permission);

    /**
     * Compter le nombre d'admins par niveau de rôle
     */
    long countByRoleLevel(String roleLevel);

    /**
     * Rechercher les Admins actifs
     */
    @Query("SELECT a FROM Admin a WHERE a.user.status = 'ACTIVE'")
    List<Admin> findActiveAdmins();
}