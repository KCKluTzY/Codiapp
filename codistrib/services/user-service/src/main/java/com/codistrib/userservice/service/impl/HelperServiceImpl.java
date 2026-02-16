package com.codistrib.userservice.service.impl;

import com.codistrib.userservice.domain.enums.AvailabilityStatus;
import com.codistrib.userservice.domain.enums.UserStatus;
import com.codistrib.userservice.domain.enums.ValidationStatus;
import com.codistrib.userservice.domain.model.Helper;
import com.codistrib.userservice.domain.model.User;
import com.codistrib.userservice.domain.repository.HelperRepository;
import com.codistrib.userservice.domain.repository.PersonDIRepository;
import com.codistrib.userservice.dto.HelperDtos;
import com.codistrib.userservice.exception.HelperNotValidatedException;
import com.codistrib.userservice.exception.UserNotFoundException;
import com.codistrib.userservice.exception.ValidationException;
import com.codistrib.userservice.service.HelperService;
import com.codistrib.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HelperServiceImpl implements HelperService {

    private final HelperRepository helperRepository;
    private final PersonDIRepository personDIRepository;
    private final UserService userService;

    @Override
    public Helper createHelper(HelperDtos.CreateHelperRequest request) {
        log.info("Création d'un nouveau Helper: {}", request.getName());
        
        // Valider que l'authId n'existe pas déjà
        if (request.getAuthId() != null && userService.authIdExists(request.getAuthId())) {
            throw new ValidationException("authId", "Un utilisateur avec cet auth_id existe déjà");
        }
        
        // Créer l'utilisateur User d'abord
        User user = User.builder()
                .authId(request.getAuthId())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .status(UserStatus.ACTIVE)  // Helper peut se connecter immédiatement
                .build();
        
        // Créer l'entité Helper
        Helper helper = Helper.builder()
                .user(user)
                .specializations(request.getSpecializations())
                .certifications(request.getCertifications())
                .languages(request.getLanguages() != null ? request.getLanguages() : List.of("fr"))
                .maxConcurrentAssists(request.getMaxConcurrentAssists() != null ? 
                        request.getMaxConcurrentAssists() : 3)
                .validationStatus(ValidationStatus.PENDING)  // En attente validation
                .availabilityStatus(AvailabilityStatus.OFFLINE)
                .rating(BigDecimal.ZERO)
                .totalAssists(0)
                .build();
        
        Helper savedHelper = helperRepository.save(helper);
        log.info("Helper créé avec succès: userId={}, validationStatus=PENDING", 
                savedHelper.getUserId());
        
        return savedHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public Helper getHelperById(UUID userId) {
        log.debug("Récupération du Helper: userId={}", userId);
        return helperRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Helper avec l'ID " + userId + " non trouvé"));
    }

    @Override
    public Helper updateHelper(HelperDtos.UpdateHelperRequest request) {
        log.info("Mise à jour du Helper: userId={}", request.getUserId());
        
        Helper helper = getHelperById(request.getUserId());
        User user = helper.getUser();
        
        // Mettre à jour User
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        
        // Mettre à jour Helper
        if (request.getSpecializations() != null) {
            helper.setSpecializations(request.getSpecializations());
        }
        if (request.getCertifications() != null) {
            helper.setCertifications(request.getCertifications());
        }
        if (request.getLanguages() != null) {
            helper.setLanguages(request.getLanguages());
        }
        if (request.getMaxConcurrentAssists() != null) {
            helper.setMaxConcurrentAssists(request.getMaxConcurrentAssists());
        }
        
        Helper updatedHelper = helperRepository.save(helper);
        log.info("Helper mis à jour avec succès: userId={}", updatedHelper.getUserId());
        
        return updatedHelper;
    }

    @Override
    public Helper updateAvailability(HelperDtos.UpdateAvailabilityRequest request) {
        log.info("Mise à jour de la disponibilité du Helper: userId={}, status={}", 
                request.getUserId(), request.getAvailabilityStatus());
        
        Helper helper = getHelperById(request.getUserId());
        helper.setAvailabilityStatus(request.getAvailabilityStatus());
        
        Helper updatedHelper = helperRepository.save(helper);
        log.info("Disponibilité mise à jour: userId={}, nouveau statut={}", 
                updatedHelper.getUserId(), updatedHelper.getAvailabilityStatus());
        
        return updatedHelper;
    }

    @Override
    public HelperDtos.ValidateHelperResponse validateHelper(HelperDtos.ValidateHelperRequest request) {
        log.info("Validation du Helper: helperId={}, approved={}", 
                request.getHelperId(), request.isApproved());
        
        Helper helper = getHelperById(request.getHelperId());
        
        // Vérifier que le Helper est en attente de validation
        if (helper.getValidationStatus() != ValidationStatus.PENDING) {
            throw new ValidationException("Helper déjà validé ou rejeté. Statut actuel: " + 
                    helper.getValidationStatus());
        }
        
        // Mettre à jour le statut
        ValidationStatus newStatus = request.isApproved() ? 
                ValidationStatus.APPROVED : ValidationStatus.REJECTED;
        helper.setValidationStatus(newStatus);
        helper.setValidatedBy(request.getValidatedBy());
        helper.setValidatedAt(LocalDateTime.now());
        
        helperRepository.save(helper);
        
        log.info("Helper validé avec succès: helperId={}, status={}", 
                request.getHelperId(), newStatus);
        
        return HelperDtos.ValidateHelperResponse.builder()
                .helperId(helper.getUserId())
                .helperName(helper.getUser().getName())
                .validationStatus(newStatus)
                .message(request.isApproved() ? 
                        "Helper approuvé avec succès. Il peut maintenant inscrire des PersonDI." :
                        "Helper rejeté.")
                .validatedAt(helper.getValidatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHelperValidated(UUID userId) {
        boolean isValidated = helperRepository.isHelperValidated(userId);
        
        if (!isValidated) {
            log.warn("Tentative d'action par Helper non validé: userId={}", userId);
            throw new HelperNotValidatedException(userId);
        }
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Helper> getHelpersByValidationStatus(ValidationStatus validationStatus) {
        log.debug("Récupération des Helpers par statut de validation: {}", validationStatus);
        return helperRepository.findByValidationStatus(validationStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Helper> getHelpersByAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        log.debug("Récupération des Helpers par disponibilité: {}", availabilityStatus);
        return helperRepository.findByAvailabilityStatus(availabilityStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public HelperDtos.AvailableHelpersResponse getAvailableHelpers(
            HelperDtos.AvailableHelpersRequest request) {
        log.debug("Recherche de Helpers disponibles avec filtres");
        
        // Récupérer tous les Helpers validés et disponibles
        List<Helper> helpers = helperRepository.findAvailableValidatedHelpers();
        
        // Appliquer les filtres optionnels
        if (request.getSpecialization() != null) {
            helpers = helpers.stream()
                    .filter(h -> h.getSpecializations() != null && 
                            h.getSpecializations().contains(request.getSpecialization()))
                    .collect(Collectors.toList());
        }
        
        if (request.getLanguage() != null) {
            helpers = helpers.stream()
                    .filter(h -> h.getLanguages() != null && 
                            h.getLanguages().contains(request.getLanguage()))
                    .collect(Collectors.toList());
        }
        
        if (request.getMinRating() != null) {
            helpers = helpers.stream()
                    .filter(h -> h.getRating().compareTo(request.getMinRating()) >= 0)
                    .collect(Collectors.toList());
        }
        
        // Convertir en HelperSummary
        List<HelperDtos.HelperSummary> summaries = helpers.stream()
                .map(this::toHelperSummary)
                .collect(Collectors.toList());
        
        log.info("Helpers disponibles trouvés: {}", summaries.size());
        
        return HelperDtos.AvailableHelpersResponse.builder()
                .totalAvailable(summaries.size())
                .helpers(summaries)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Helper> getHelpersWithCapacity() {
        log.debug("Récupération des Helpers avec capacité disponible");
        return helperRepository.findHelpersWithCapacity();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Helper> getHelpersBySpecialization(String specialization) {
        log.debug("Récupération des Helpers par spécialisation: {}", specialization);
        return helperRepository.findBySpecialization(specialization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Helper> getHelpersByLanguage(String language) {
        log.debug("Récupération des Helpers par langue: {}", language);
        return helperRepository.findByLanguage(language);
    }

    @Override
    @Transactional(readOnly = true)
    public HelperDtos.HelperStats getHelperStats(UUID userId) {
        log.debug("Récupération des statistiques du Helper: userId={}", userId);
        
        Helper helper = getHelperById(userId);
        
        // Compter les PersonDI assignées
        long totalAssigned = personDIRepository.countByAssignedHelperId(userId);
        
        // Compter celles avec credentials
        long withCredentials = personDIRepository.findByAssignedHelperId(userId).stream()
                .filter(p -> p.getUser().getAuthId() != null)
                .count();
        
        // Compter celles en attente
        long pendingCredentials = totalAssigned - withCredentials;
        
        return HelperDtos.HelperStats.builder()
                .helperId(userId)
                .helperName(helper.getUser().getName())
                .totalAssists(helper.getTotalAssists())
                .rating(helper.getRating())
                .currentAssignedPersons((int) totalAssigned)
                .personsWithCredentials((int) withCredentials)
                .personsPendingCredentials((int) pendingCredentials)
                .lastActiveAt(helper.getUser().getUpdatedAt())
                .build();
    }

    @Override
    public void incrementTotalAssists(UUID userId) {
        log.debug("Incrémentation des assistances du Helper: userId={}", userId);
        
        Helper helper = getHelperById(userId);
        helper.setTotalAssists(helper.getTotalAssists() + 1);
        helperRepository.save(helper);
        
        log.info("Assistances incrémentées: userId={}, nouveau total={}", 
                userId, helper.getTotalAssists());
    }

    @Override
    public void updateRating(UUID userId, Double newRating) {
        log.info("Mise à jour de la note du Helper: userId={}, newRating={}", userId, newRating);
        
        if (newRating < 0.0 || newRating > 5.0) {
            throw new ValidationException("rating", "La note doit être entre 0.0 et 5.0");
        }
        
        Helper helper = getHelperById(userId);
        helper.setRating(BigDecimal.valueOf(newRating));
        helperRepository.save(helper);
        
        log.info("Note mise à jour: userId={}, nouvelle note={}", userId, newRating);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByValidationStatus(ValidationStatus validationStatus) {
        return helperRepository.countByValidationStatus(validationStatus);
    }

    /**
     * Méthode utilitaire pour convertir Helper en HelperSummary
     */
    private HelperDtos.HelperSummary toHelperSummary(Helper helper) {
        long assignedCount = personDIRepository.countByAssignedHelperId(helper.getUserId());
        boolean canTakeMore = assignedCount < helper.getMaxConcurrentAssists();
        
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
                .canTakeMore(canTakeMore)
                .build();
    }
}