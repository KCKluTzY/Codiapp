package com.codistrib.userservice.domain.repository;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.enums.DisabilityLevel;
import com.codistrib.userservice.domain.model.PersonDI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonDIRepository extends JpaRepository<PersonDI, UUID> {

    /**
     * Rechercher une PersonDI par user_id
     */
    Optional<PersonDI> findByUserId(UUID userId);

    /**
     * Rechercher toutes les PersonDI assignées à un Helper spécifique
     */
    List<PersonDI> findByAssignedHelperId(UUID assignedHelperId);

    /**
     * Rechercher les PersonDI par niveau de déficience
     */
    List<PersonDI> findByDisabilityLevel(DisabilityLevel disabilityLevel);

    /**
     * Rechercher les PersonDI par statut de demande de credentials
     */
    List<PersonDI> findByCredentialRequestStatus(CredentialRequestStatus status);

    /**
     * Rechercher les PersonDI sans credentials
     */
    @Query("SELECT p FROM PersonDI p WHERE p.user.authId IS NULL")
    List<PersonDI> findPersonsWithoutCredentials();

    /**
     * Rechercher les PersonDI avec credentials actifs
     */
    @Query("SELECT p FROM PersonDI p WHERE p.user.authId IS NOT NULL " +
           "AND p.user.status = 'ACTIVE'")
    List<PersonDI> findActivePersonsWithCredentials();

    /**
     * Compter le nombre de PersonDI assignées à un Helper
     */
    long countByAssignedHelperId(UUID assignedHelperId);

    /**
     * Rechercher les PersonDI par email du tuteur
     */
    List<PersonDI> findByGuardianEmailContainingIgnoreCase(String guardianEmail);

    /**
     * Vérifier si une PersonDI avec un email tuteur spécifique existe
     */
    boolean existsByGuardianEmail(String guardianEmail);

    /**
     * Rechercher les PersonDI créées par un Helper spécifique
     */
    @Query("SELECT p FROM PersonDI p WHERE p.user.createdBy = :createdBy")
    List<PersonDI> findByCreatedBy(@Param("createdBy") UUID createdBy);

    /**
     * Rechercher les PersonDI avec des demandes de credentials en attente
     * pour un Helper spécifique
     */
    @Query("SELECT p FROM PersonDI p WHERE p.user.createdBy = :helperId " +
           "AND p.credentialRequestStatus = 'PENDING'")
    List<PersonDI> findPendingCredentialsByHelper(@Param("helperId") UUID helperId);
}