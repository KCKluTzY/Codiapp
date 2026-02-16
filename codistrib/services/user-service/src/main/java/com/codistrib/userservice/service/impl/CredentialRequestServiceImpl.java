package com.codistrib.userservice.service.impl;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.model.CredentialRequest;
import com.codistrib.userservice.domain.model.PersonDI;
import com.codistrib.userservice.domain.repository.CredentialRequestRepository;
import com.codistrib.userservice.dto.CredentialRequestDtos;
import com.codistrib.userservice.exception.UserNotFoundException;
import com.codistrib.userservice.exception.ValidationException;
import com.codistrib.userservice.grpc.AuthServiceGrpcClient;
import com.codistrib.userservice.service.CredentialRequestService;
import com.codistrib.userservice.service.PersonDIService;
import com.codistrib.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service CredentialRequest
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CredentialRequestServiceImpl implements CredentialRequestService {

    private final CredentialRequestRepository credentialRequestRepository;
    private final UserService userService;
    private final PersonDIService personDIService;
    private final AuthServiceGrpcClient authServiceGrpcClient;

    @Override
    public CredentialRequest createCredentialRequest(UUID userId, UUID requestedBy, String requestedEmail) {
        log.info("Création d'une demande de credentials: userId={}, requestedBy={}, email={}", 
                userId, requestedBy, requestedEmail);
        
        // Vérifier que la PersonDI existe
        PersonDI personDI = personDIService.getPersonDIById(userId);
        
        // Vérifier qu'il n'y a pas déjà une demande PENDING
        if (hasPendingRequest(userId)) {
            throw new ValidationException("Une demande de credentials est déjà en attente pour cette PersonDI");
        }
        
        // Vérifier que l'email n'est pas déjà demandé
        if (isEmailAlreadyRequested(requestedEmail)) {
            throw new ValidationException("requestedEmail", 
                    "Cet email est déjà utilisé dans une autre demande en attente");
        }
        
        // Créer la demande
        CredentialRequest request = CredentialRequest.builder()
                .userId(userId)
                .requestedBy(requestedBy)
                .requestedEmail(requestedEmail)
                .status(CredentialRequestStatus.PENDING)
                .build();
        
        CredentialRequest savedRequest = credentialRequestRepository.save(request);
        log.info("Demande de credentials créée avec succès: requestId={}", savedRequest.getRequestId());
        
        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public CredentialRequest getCredentialRequestById(UUID requestId) {
        log.debug("Récupération de la demande: requestId={}", requestId);
        return credentialRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Demande avec l'ID " + requestId + " non trouvée"));
    }

    @Override
    public CredentialRequestDtos.ReviewCredentialRequestResponse reviewCredentialRequest(
            CredentialRequestDtos.ReviewCredentialRequestRequest request) {
        log.info("Traitement de la demande de credentials: requestId={}, approved={}", 
                request.getRequestId(), request.isApproved());
        
        CredentialRequest credentialRequest = getCredentialRequestById(request.getRequestId());
        
        // Vérifier que la demande est en attente
        if (credentialRequest.getStatus() != CredentialRequestStatus.PENDING) {
            throw new ValidationException("Cette demande a déjà été traitée. Statut: " + 
                    credentialRequest.getStatus());
        }
        
        PersonDI personDI = personDIService.getPersonDIById(credentialRequest.getUserId());
        
        if (request.isApproved()) {
            // APPROUVÉ - Créer les credentials dans Auth Service
            return approveCredentialRequest(credentialRequest, personDI, request);
        } else {
            // REJETÉ
            return rejectCredentialRequest(credentialRequest, request);
        }
    }

    /**
     * Approuver une demande et créer les credentials
     */
    private CredentialRequestDtos.ReviewCredentialRequestResponse approveCredentialRequest(
            CredentialRequest credentialRequest,
            PersonDI personDI,
            CredentialRequestDtos.ReviewCredentialRequestRequest request) {
        
        log.info("Approbation de la demande: requestId={}", credentialRequest.getRequestId());
        
        // Utiliser l'email fourni ou l'email de la demande
        String email = request.getEmail() != null ? request.getEmail() : credentialRequest.getRequestedEmail();
        
        // Générer un mot de passe temporaire sécurisé
        String temporaryPassword = generateTemporaryPassword();
        
        // Appeler Auth Service via gRPC pour créer les credentials
        UUID authId = createAuthCredentials(email, temporaryPassword, personDI);
        
        // Mettre à jour la demande
        credentialRequest.setStatus(CredentialRequestStatus.APPROVED);
        credentialRequest.setReviewedBy(request.getReviewedBy());
        credentialRequest.setReviewedAt(LocalDateTime.now());
        credentialRequest.setAdminNotes(request.getAdminNotes());
        credentialRequestRepository.save(credentialRequest);
        
        // Mettre à jour la PersonDI
        personDIService.updateCredentialRequestStatus(
                credentialRequest.getUserId(), 
                CredentialRequestStatus.APPROVED);
        
        // Mettre à jour le User avec l'authId
        userService.updateAuthId(credentialRequest.getUserId(), authId);
        
        log.info("Credentials créés avec succès: userId={}, authId={}", 
                credentialRequest.getUserId(), authId);
        
        // TODO: Envoyer email au tuteur si sendCredentialsByEmail = true
        if (request.isSendCredentialsByEmail()) {
            sendCredentialsEmail(personDI, email, temporaryPassword);
        }
        
        return CredentialRequestDtos.ReviewCredentialRequestResponse.builder()
                .requestId(credentialRequest.getRequestId())
                .userId(credentialRequest.getUserId())
                .personName(personDI.getUser().getName())
                .status(CredentialRequestStatus.APPROVED)
                .authId(authId)
                .temporaryPassword(temporaryPassword)
                .credentialsSent(request.isSendCredentialsByEmail())
                .message("Credentials créés avec succès. Le tuteur peut maintenant se connecter.")
                .build();
    }

    /**
     * Rejeter une demande
     */
    private CredentialRequestDtos.ReviewCredentialRequestResponse rejectCredentialRequest(
            CredentialRequest credentialRequest,
            CredentialRequestDtos.ReviewCredentialRequestRequest request) {
        
        log.info("Rejet de la demande: requestId={}", credentialRequest.getRequestId());
        
        credentialRequest.setStatus(CredentialRequestStatus.REJECTED);
        credentialRequest.setReviewedBy(request.getReviewedBy());
        credentialRequest.setReviewedAt(LocalDateTime.now());
        credentialRequest.setAdminNotes(request.getAdminNotes());
        credentialRequestRepository.save(credentialRequest);
        
        // Mettre à jour la PersonDI
        personDIService.updateCredentialRequestStatus(
                credentialRequest.getUserId(), 
                CredentialRequestStatus.REJECTED);
        
        PersonDI personDI = personDIService.getPersonDIById(credentialRequest.getUserId());
        
        return CredentialRequestDtos.ReviewCredentialRequestResponse.builder()
                .requestId(credentialRequest.getRequestId())
                .userId(credentialRequest.getUserId())
                .personName(personDI.getUser().getName())
                .status(CredentialRequestStatus.REJECTED)
                .credentialsSent(false)
                .message("Demande rejetée. Raison: " + 
                        (request.getAdminNotes() != null ? request.getAdminNotes() : "Non spécifiée"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CredentialRequest> getCredentialRequestsByStatus(CredentialRequestStatus status) {
        log.debug("Récupération des demandes par statut: {}", status);
        return credentialRequestRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public CredentialRequestDtos.PendingCredentialRequestsResponse getPendingRequests() {
        log.debug("Récupération de toutes les demandes en attente");
        
        List<CredentialRequest> pendingRequests = credentialRequestRepository.findPendingRequestsOrderedByDate();
        
        // Calculer le nombre de jours de la plus ancienne demande
        int oldestDays = 0;
        if (!pendingRequests.isEmpty()) {
            LocalDateTime oldest = pendingRequests.get(0).getCreatedAt();
            oldestDays = (int) ChronoUnit.DAYS.between(oldest, LocalDateTime.now());
        }
        
        // Convertir en summary
        List<CredentialRequestDtos.CredentialRequestSummary> summaries = pendingRequests.stream()
                .map(this::toCredentialRequestSummary)
                .collect(Collectors.toList());
        
        return CredentialRequestDtos.PendingCredentialRequestsResponse.builder()
                .totalPending(summaries.size())
                .oldestDays(oldestDays)
                .requests(summaries)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CredentialRequest> getCredentialRequestsByHelper(UUID requestedBy) {
        log.debug("Récupération des demandes du Helper: {}", requestedBy);
        return credentialRequestRepository.findByRequestedBy(requestedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public CredentialRequest getPendingRequestByUserId(UUID userId) {
        return credentialRequestRepository.findPendingRequestByUserId(userId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingRequest(UUID userId) {
        return credentialRequestRepository.hasPendingRequest(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAlreadyRequested(String email) {
        return credentialRequestRepository.isEmailAlreadyRequested(email);
    }

    @Override
    @Transactional(readOnly = true)
    public CredentialRequestDtos.CredentialRequestStats getStats() {
        log.debug("Calcul des statistiques des demandes de credentials");
        
        long total = credentialRequestRepository.count();
        long pending = credentialRequestRepository.countByStatus(CredentialRequestStatus.PENDING);
        long approved = credentialRequestRepository.countByStatus(CredentialRequestStatus.APPROVED);
        long rejected = credentialRequestRepository.countByStatus(CredentialRequestStatus.REJECTED);
        
        double approvalRate = (total > 0) ? ((double) approved / total) * 100 : 0.0;
        
        // Calculer la durée moyenne de traitement
        int averageProcessingDays = calculateAverageProcessingDays();
        
        return CredentialRequestDtos.CredentialRequestStats.builder()
                .totalRequests((int) total)
                .pendingRequests((int) pending)
                .approvedRequests((int) approved)
                .rejectedRequests((int) rejected)
                .approvalRate(approvalRate)
                .averageProcessingDays(averageProcessingDays)
                .build();
    }
    
    /**
     * Calculer la durée moyenne de traitement en jours
     * Pour les demandes qui ont été traitées (APPROVED ou REJECTED)
     */
    private int calculateAverageProcessingDays() {
        // Récupérer toutes les demandes traitées
        List<CredentialRequest> approvedRequests = credentialRequestRepository
                .findByStatus(CredentialRequestStatus.APPROVED);
        List<CredentialRequest> rejectedRequests = credentialRequestRepository
                .findByStatus(CredentialRequestStatus.REJECTED);
        
        // Combiner les deux listes
        List<CredentialRequest> processedRequests = new java.util.ArrayList<>();
        processedRequests.addAll(approvedRequests);
        processedRequests.addAll(rejectedRequests);
        
        // Si aucune demande traitée, retourner 0
        if (processedRequests.isEmpty()) {
            return 0;
        }
        
        // Calculer la durée pour chaque demande
        long totalDays = processedRequests.stream()
                .filter(cr -> cr.getReviewedAt() != null)  // S'assurer que reviewedAt existe
                .mapToLong(cr -> {
                    LocalDateTime createdAt = cr.getCreatedAt();
                    LocalDateTime reviewedAt = cr.getReviewedAt();
                    return java.time.temporal.ChronoUnit.DAYS.between(createdAt, reviewedAt);
                })
                .sum();
        
        // Compter combien de demandes ont effectivement une date de traitement
        long countWithReviewDate = processedRequests.stream()
                .filter(cr -> cr.getReviewedAt() != null)
                .count();
        
        // Calculer la moyenne
        if (countWithReviewDate == 0) {
            return 0;
        }
        
        int average = (int) (totalDays / countWithReviewDate);
        log.debug("Durée moyenne de traitement calculée: {} jours (sur {} demandes)", 
                average, countWithReviewDate);
        
        return average;
    }

    /**
     * Méthode utilitaire pour convertir en Summary
     */
    private CredentialRequestDtos.CredentialRequestSummary toCredentialRequestSummary(
            CredentialRequest request) {
        
        String personName = userService.getUserById(request.getUserId()).getName();
        String helperName = userService.getUserById(request.getRequestedBy()).getName();
        
        int daysSinceCreated = (int) ChronoUnit.DAYS.between(request.getCreatedAt(), LocalDateTime.now());
        
        return CredentialRequestDtos.CredentialRequestSummary.builder()
                .requestId(request.getRequestId())
                .userId(request.getUserId())
                .personName(personName)
                .helperName(helperName)
                .requestedEmail(request.getRequestedEmail())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .daysSinceCreated(daysSinceCreated)
                .build();
    }

    /**
     * Générer un mot de passe temporaire sécurisé
     * 
     * Format : 12 caractères
     * - Au moins 1 majuscule
     * - Au moins 1 minuscule
     * - Au moins 1 chiffre
     * - Au moins 1 caractère spécial
     */
    private String generateTemporaryPassword() {
        // Caractères autorisés par catégorie
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%&*";
        
        // Utiliser SecureRandom pour cryptographie
        java.security.SecureRandom random = new java.security.SecureRandom();
        
        StringBuilder password = new StringBuilder(12);
        
        // Garantir au moins 1 caractère de chaque catégorie
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));
        
        // Compléter avec des caractères aléatoires (8 caractères restants)
        String allChars = uppercase + lowercase + digits + special;
        for (int i = 0; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Mélanger les caractères pour que les premiers ne soient pas toujours prévisibles
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        String generatedPassword = new String(passwordArray);
        log.debug("Mot de passe temporaire généré (longueur: {})", generatedPassword.length());
        
        return generatedPassword;
    }

    /**
     * Créer des credentials dans Auth Service via gRPC
     * 
     * @param email Email pour les credentials
     * @param password Mot de passe temporaire
     * @param personDI PersonDI concernée
     * @return UUID authId créé dans Auth Service
     */
    private UUID createAuthCredentials(String email, String password, PersonDI personDI) {
        log.info("Création de credentials dans Auth Service pour PersonDI: {} (userId={})",
                personDI.getUser().getName(), personDI.getUserId());
        
        try {
            // Appel gRPC vers Auth Service
            UUID authId = authServiceGrpcClient.createCredentialsForPersonDI(
                    personDI.getUserId(),
                    email,
                    password
            );
            
            log.info("✅ Credentials créés avec succès dans Auth Service: authId={}", authId);
            return authId;
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création des credentials dans Auth Service: {}", 
                    e.getMessage(), e);
            throw new RuntimeException("Impossible de créer les credentials dans Auth Service. " +
                    "Veuillez vérifier que Auth Service est accessible.", e);
        }
    }

    /**
     * Envoyer email au tuteur avec les credentials
     * 
     * TODO: Implémenter l'envoi email via Notification Service
     * 
     * Code à implémenter :
     * 
     * @Autowired
     * private NotificationServiceGrpcClient notificationServiceGrpcClient;
     * 
     * notificationServiceGrpcClient.sendEmail(
     *     SendEmailRequest.builder()
     *         .to(email)
     *         .subject("Accès créé pour " + personDI.getUser().getName() + " - CODI'strib")
     *         .body(generateEmailBody(personDI, email, temporaryPassword))
     *         .priority("HIGH")
     *         .build()
     * );
     * 
     * Le Notification Service doit exposer un endpoint gRPC :
     * rpc SendEmail(SendEmailRequest) returns (SendEmailResponse);
     */
    private void sendCredentialsEmail(PersonDI personDI, String email, String temporaryPassword) {
        log.warn("⚠️ TODO: Envoyer email via Notification Service");
        log.info("   Destinataire: {} ({})", personDI.getGuardianName(), email);
        log.info("   Sujet: Accès créé pour {} - CODI'strib", personDI.getUser().getName());
        
        // Générer le corps de l'email
        String emailBody = generateEmailBody(personDI, email, temporaryPassword);
        log.info("   Corps de l'email généré (longueur: {} caractères)", emailBody.length());
        log.debug("   Aperçu: {}", emailBody.substring(0, Math.min(100, emailBody.length())));
        
        // TODO: Appeler Notification Service pour envoyer réellement l'email
        log.info("   Email NON ENVOYÉ (simulation uniquement)");
    }
    
    /**
     * Générer le corps de l'email avec les credentials
     */
    private String generateEmailBody(PersonDI personDI, String email, String temporaryPassword) {
        return String.format("""
                Bonjour %s,
                
                Les identifiants pour %s ont été créés avec succès.
                
                📧 Email : %s
                🔐 Mot de passe temporaire : %s
                
                ⚠️ IMPORTANT : 
                - Ce mot de passe est temporaire et doit être changé lors de la première connexion.
                - Conservez ces informations en lieu sûr.
                - Ne partagez jamais votre mot de passe.
                
                Pour vous connecter :
                1. Rendez-vous sur l'application CODI'strib
                2. Utilisez l'email et le mot de passe ci-dessus
                3. Vous serez invité à créer un nouveau mot de passe
                
                En cas de difficulté, contactez votre aidant ou l'administrateur.
                
                Cordialement,
                L'équipe CODI'strib
                
                ---
                Ceci est un email automatique, merci de ne pas y répondre.
                """,
                personDI.getGuardianName(),
                personDI.getUser().getName(),
                email,
                temporaryPassword
        );
    }
}