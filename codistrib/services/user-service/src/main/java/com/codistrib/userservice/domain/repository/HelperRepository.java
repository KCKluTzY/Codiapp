package com.codistrib.userservice.domain.repository;

import com.codistrib.userservice.domain.enums.AvailabilityStatus;
import com.codistrib.userservice.domain.enums.ValidationStatus;
import com.codistrib.userservice.domain.model.Helper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HelperRepository extends JpaRepository<Helper, UUID> {

    /**
     * Rechercher un Helper par user_id
     */
    Optional<Helper> findByUserId(UUID userId);

    /**
     * Rechercher les Helpers par statut de validation
     * Utile pour les admins : voir tous les Helpers en attente de validation
     */
    List<Helper> findByValidationStatus(ValidationStatus validationStatus);

    /**
     * Rechercher les Helpers par statut de disponibilité
     */
    List<Helper> findByAvailabilityStatus(AvailabilityStatus availabilityStatus);

    /**
     * Rechercher les Helpers validés et disponibles
     */
    @Query("SELECT h FROM Helper h WHERE h.validationStatus = 'APPROVED' " +
           "AND h.availabilityStatus = 'AVAILABLE' " +
           "AND h.user.status = 'ACTIVE'")
    List<Helper> findAvailableValidatedHelpers();

    /**
     * Rechercher les Helpers par spécialisation
     * Utilise une requête SQL native car JPQL ne supporte pas bien ANY() avec arrays
     */
    @Query(value = "SELECT * FROM helpers WHERE :specialization = ANY(specializations)", nativeQuery = true)
    List<Helper> findBySpecialization(@Param("specialization") String specialization);

    /**
     * Rechercher les Helpers parlant une langue spécifique
     * Utilise une requête SQL native car JPQL ne supporte pas bien ANY() avec arrays
     */
    @Query(value = "SELECT * FROM helpers WHERE :language = ANY(languages)", nativeQuery = true)
    List<Helper> findByLanguage(@Param("language") String language);

    /**
     * Rechercher les Helpers avec une note minimale
     */
    List<Helper> findByRatingGreaterThanEqual(BigDecimal minRating);

    /**
     * Rechercher les Helpers validés par un Admin spécifique
     */
    List<Helper> findByValidatedBy(UUID validatedBy);

    /**
     * Compter le nombre de Helpers par statut de validation
     */
    long countByValidationStatus(ValidationStatus validationStatus);

    /**
     * Vérifier si un Helper est validé
     */
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
           "FROM Helper h WHERE h.userId = :userId AND h.validationStatus = 'APPROVED'")
    boolean isHelperValidated(@Param("userId") UUID userId);

    /**
     * Rechercher les meilleurs Helpers (note élevée, beaucoup d'assists)
     */
    @Query("SELECT h FROM Helper h WHERE h.validationStatus = 'APPROVED' " +
           "AND h.rating >= :minRating " +
           "AND h.totalAssists >= :minAssists " +
           "ORDER BY h.rating DESC, h.totalAssists DESC " +
           "LIMIT :limit")
    List<Helper> findTopHelpers(@Param("minRating") BigDecimal minRating,
                                 @Param("minAssists") Integer minAssists,
                                 @Param("limit") int limit);

    /**
     * Rechercher les Helpers disponibles avec capacité restante
     */
    @Query("SELECT h FROM Helper h WHERE h.validationStatus = 'APPROVED' " +
           "AND h.availabilityStatus = 'AVAILABLE' " +
           "AND (SELECT COUNT(p) FROM PersonDI p WHERE p.assignedHelperId = h.userId) < h.maxConcurrentAssists")
    List<Helper> findHelpersWithCapacity();
}