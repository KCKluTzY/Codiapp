package com.codistrib.userservice.service.impl;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.enums.UserStatus;
import com.codistrib.userservice.domain.model.PersonDI;
import com.codistrib.userservice.domain.model.User;
import com.codistrib.userservice.domain.repository.PersonDIRepository;
import com.codistrib.userservice.dto.PersonDIDtos;
import com.codistrib.userservice.exception.UserNotFoundException;
import com.codistrib.userservice.exception.ValidationException;
import com.codistrib.userservice.service.CredentialRequestService;
import com.codistrib.userservice.service.HelperService;
import com.codistrib.userservice.service.PersonDIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PersonDIServiceImpl implements PersonDIService {

    private final PersonDIRepository personDIRepository;
    private final HelperService helperService;
    private final CredentialRequestService credentialRequestService;

    /**
     * Constructeur avec @Lazy pour casser la dépendance circulaire
     * PersonDIService ← → CredentialRequestService
     */
    public PersonDIServiceImpl(
            PersonDIRepository personDIRepository,
            HelperService helperService,
            @Lazy CredentialRequestService credentialRequestService) {
        this.personDIRepository = personDIRepository;
        this.helperService = helperService;
        this.credentialRequestService = credentialRequestService;
    }

    @Override
    public PersonDI createPersonDI(PersonDIDtos.CreatePersonDIRequest request) {
        log.info("Création d'une nouvelle PersonDI: {}", request.getName());
        
        // VALIDATION CRITIQUE: Vérifier que le Helper est validé
        if (!helperService.isHelperValidated(request.getCreatedBy())) {
            log.error("Tentative de création PersonDI par Helper non validé: helperId={}", 
                    request.getCreatedBy());
            throw new ValidationException("Le Helper doit être validé avant de créer une PersonDI");
        }
        
        // Vérifier que l'email tuteur n'existe pas déjà
        if (guardianEmailExists(request.getGuardianEmail())) {
            throw new ValidationException("guardianEmail", 
                    "Un tuteur avec cet email existe déjà dans le système");
        }
        
        // Créer l'utilisateur User d'abord
        User user = User.builder()
                .authId(null)  // Pas encore de credentials
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .status(UserStatus.PENDING_CREDENTIALS)  // En attente de credentials
                .createdBy(request.getCreatedBy())  // Le Helper créateur
                .build();
        
        // Créer l'entité PersonDI
        PersonDI personDI = PersonDI.builder()
                .user(user)
                .disabilityLevel(request.getDisabilityLevel())
                .guardianName(request.getGuardianName())
                .guardianPhone(request.getGuardianPhone())
                .guardianEmail(request.getGuardianEmail())
                .preferredCommunication(request.getPreferredCommunication())
                .accessibilitySettings(request.getAccessibilitySettings())
                .assignedHelperId(request.getCreatedBy())  // Helper créateur devient Helper assigné
                .credentialRequestStatus(CredentialRequestStatus.PENDING)
                .build();
        
        PersonDI savedPersonDI = personDIRepository.save(personDI);
        log.info("PersonDI créée avec succès: userId={}, assignedHelper={}", 
                savedPersonDI.getUserId(), savedPersonDI.getAssignedHelperId());
        
        // Créer automatiquement une CredentialRequest
        credentialRequestService.createCredentialRequest(
                savedPersonDI.getUserId(),
                request.getCreatedBy(),
                request.getRequestedEmail()
        );
        
        log.info("CredentialRequest créée pour PersonDI: userId={}", savedPersonDI.getUserId());
        
        return savedPersonDI;
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDI getPersonDIById(UUID userId) {
        log.debug("Récupération de la PersonDI: userId={}", userId);
        return personDIRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("PersonDI avec l'ID " + userId + " non trouvée"));
    }

    @Override
    public PersonDI updatePersonDI(PersonDIDtos.UpdatePersonDIRequest request) {
        log.info("Mise à jour de la PersonDI: userId={}", request.getUserId());
        
        PersonDI personDI = getPersonDIById(request.getUserId());
        User user = personDI.getUser();
        
        // Mettre à jour User
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        // Mettre à jour PersonDI
        if (request.getGuardianName() != null) {
            personDI.setGuardianName(request.getGuardianName());
        }
        if (request.getGuardianPhone() != null) {
            personDI.setGuardianPhone(request.getGuardianPhone());
        }
        if (request.getGuardianEmail() != null) {
            personDI.setGuardianEmail(request.getGuardianEmail());
        }
        if (request.getPreferredCommunication() != null) {
            personDI.setPreferredCommunication(request.getPreferredCommunication());
        }
        if (request.getAccessibilitySettings() != null) {
            personDI.setAccessibilitySettings(request.getAccessibilitySettings());
        }
        if (request.getAssignedHelperId() != null) {
            personDI.setAssignedHelperId(request.getAssignedHelperId());
        }
        
        PersonDI updatedPersonDI = personDIRepository.save(personDI);
        log.info("PersonDI mise à jour avec succès: userId={}", updatedPersonDI.getUserId());
        
        return updatedPersonDI;
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDIDtos.HelperPersonDIListResponse getPersonsDIByHelper(UUID helperId) {
        log.debug("Récupération des PersonDI du Helper: helperId={}", helperId);
        
        List<PersonDI> persons = personDIRepository.findByAssignedHelperId(helperId);
        
        // Statistiques
        int total = persons.size();
        int withCredentials = (int) persons.stream()
                .filter(p -> p.getUser().getAuthId() != null)
                .count();
        int pendingCredentials = total - withCredentials;
        
        // Convertir en PersonDISummary
        List<PersonDIDtos.PersonDISummary> summaries = persons.stream()
                .map(this::toPersonDISummary)
                .collect(Collectors.toList());
        
        // Récupérer le nom du Helper
        String helperName = helperService.getHelperById(helperId).getUser().getName();
        
        return PersonDIDtos.HelperPersonDIListResponse.builder()
                .helperId(helperId)
                .helperName(helperName)
                .totalPersons(total)
                .personsWithCredentials(withCredentials)
                .personsPendingCredentials(pendingCredentials)
                .persons(summaries)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDI> getPersonsDIByCredentialRequestStatus(CredentialRequestStatus status) {
        log.debug("Récupération des PersonDI par statut de credential request: {}", status);
        return personDIRepository.findByCredentialRequestStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDI> getPersonsDIWithoutCredentials() {
        log.debug("Récupération des PersonDI sans credentials");
        return personDIRepository.findPersonsWithoutCredentials();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDI> getActivePersonsDIWithCredentials() {
        log.debug("Récupération des PersonDI actives avec credentials");
        return personDIRepository.findActivePersonsWithCredentials();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDI> getPersonsDICreatedBy(UUID createdBy) {
        log.debug("Récupération des PersonDI créées par: {}", createdBy);
        return personDIRepository.findByCreatedBy(createdBy);
    }

    @Override
    public PersonDI updateCredentialRequestStatus(UUID userId, CredentialRequestStatus status) {
        log.info("Mise à jour du statut de credential request: userId={}, status={}", userId, status);
        
        PersonDI personDI = getPersonDIById(userId);
        personDI.setCredentialRequestStatus(status);
        
        PersonDI updated = personDIRepository.save(personDI);
        log.info("Statut credential request mis à jour: userId={}, nouveau statut={}", 
                userId, status);
        
        return updated;
    }

    @Override
    public PersonDI assignHelper(UUID personDIId, UUID helperId) {
        log.info("Assignation d'un Helper à une PersonDI: personDIId={}, helperId={}", 
                personDIId, helperId);
        
        // Vérifier que le Helper existe et est validé
        helperService.isHelperValidated(helperId);
        
        PersonDI personDI = getPersonDIById(personDIId);
        personDI.setAssignedHelperId(helperId);
        
        PersonDI updated = personDIRepository.save(personDI);
        log.info("Helper assigné avec succès: personDIId={}, helperId={}", 
                personDIId, helperId);
        
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean guardianEmailExists(String guardianEmail) {
        return personDIRepository.existsByGuardianEmail(guardianEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAssignedHelper(UUID helperId) {
        return personDIRepository.countByAssignedHelperId(helperId);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDIDtos.PersonDISummary getPersonDISummary(UUID userId) {
        log.debug("Récupération du summary PersonDI: userId={}", userId);
        PersonDI personDI = getPersonDIById(userId);
        return toPersonDISummary(personDI);
    }

    /**
     * Méthode utilitaire pour convertir PersonDI en PersonDISummary
     */
    private PersonDIDtos.PersonDISummary toPersonDISummary(PersonDI personDI) {
        String helperName = null;
        if (personDI.getAssignedHelperId() != null) {
            try {
                helperName = helperService.getHelperById(personDI.getAssignedHelperId())
                        .getUser().getName();
            } catch (Exception e) {
                log.warn("Impossible de récupérer le nom du Helper: helperId={}", 
                        personDI.getAssignedHelperId());
            }
        }
        
        return PersonDIDtos.PersonDISummary.builder()
                .userId(personDI.getUserId())
                .name(personDI.getUser().getName())
                .profilePictureUrl(personDI.getUser().getProfilePictureUrl())
                .disabilityLevel(personDI.getDisabilityLevel())
                .assignedHelperId(personDI.getAssignedHelperId())
                .assignedHelperName(helperName)
                .credentialRequestStatus(personDI.getCredentialRequestStatus())
                .hasCredentials(personDI.getUser().getAuthId() != null)
                .build();
    }
}