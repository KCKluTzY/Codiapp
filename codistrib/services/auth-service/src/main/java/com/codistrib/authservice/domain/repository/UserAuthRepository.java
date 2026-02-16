package com.codistrib.authservice.domain.repository;

import com.codistrib.authservice.domain.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {
    Optional<UserAuth> findByEmailIgnoreCase(String email);
    Optional<UserAuth> findByUsernameIgnoreCase(String username);
}