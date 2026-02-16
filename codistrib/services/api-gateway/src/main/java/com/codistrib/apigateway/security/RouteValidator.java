package com.codistrib.apigateway.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class RouteValidator {

    private List<String> publicRoutes = new ArrayList<>();


    private Map<String, List<String>> roleRoutes = new HashMap<>();

    /**
     * Matcher pour les patterns de type Ant (avec wildcards).
     * Ex: /api/v1/admin/** matche /api/v1/admin/users, /api/v1/admin/config, etc.
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Vérifie si une route est publique (pas de JWT requis).
     * 
     */
    public boolean isPublicRoute(String path) {
        boolean isPublic = publicRoutes.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
        
        if (isPublic) {
            log.debug("Route publique détectée: {}", path);
        }
        
        return isPublic;
    }

    /**
     * Vérifie si un rôle peut accéder à une route.
     * 
     * Logique :
     * 1. ADMINISTRATOR peut accéder à TOUT
     * 2. Sinon, on vérifie si la route est dans la liste du rôle
     * 3. Si la route n'est dans aucune liste, elle est accessible à tous les authentifiés
     * 
     */
    public boolean canAccess(String path, String role) {
        if (role == null) {
            log.warn("Rôle null pour la route: {}", path);
            return false;
        }

        // ADMINISTRATOR peut tout faire
        if ("ADMINISTRATOR".equals(role)) {
            log.debug("Accès ADMINISTRATOR autorisé pour: {}", path);
            return true;
        }

        // Vérifie si la route est restreinte à un rôle spécifique
        for (Map.Entry<String, List<String>> entry : roleRoutes.entrySet()) {
            String requiredRole = entry.getKey();
            List<String> routes = entry.getValue();
            
            boolean routeMatches = routes.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
            
            if (routeMatches) {
                // La route est restreinte, vérifie si le rôle correspond
                if (requiredRole.equals(role)) {
                    log.debug("Accès autorisé pour {} sur {}", role, path);
                    return true;
                }
                
                // La route est restreinte à un autre rôle
                log.debug("Route {} restreinte à {}, utilisateur a le rôle {}", 
                    path, requiredRole, role);
                return false;
            }
        }

        // Route non listée = accessible à tous les utilisateurs authentifiés
        log.debug("Route {} accessible à tout utilisateur authentifié", path);
        return true;
    }

    /**
     * Retourne les rôles autorisés pour une route.
     * Utile pour les messages d'erreur.
     * 
     */
    public List<String> getAllowedRoles(String path) {
        List<String> allowedRoles = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : roleRoutes.entrySet()) {
            String role = entry.getKey();
            List<String> routes = entry.getValue();
            
            boolean routeMatches = routes.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
            
            if (routeMatches) {
                allowedRoles.add(role);
            }
        }
        
        // ADMINISTRATOR est toujours autorisé
        if (!allowedRoles.isEmpty() && !allowedRoles.contains("ADMINISTRATOR")) {
            allowedRoles.add("ADMINISTRATOR");
        }
        
        return allowedRoles;
    }
}