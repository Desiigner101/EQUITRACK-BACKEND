package io.equitrack.repository;

import io.equitrack.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository; // Base CRUD operations
import java.util.Optional;                                   // Safe null handling

/**
 * Repository para sa ProfileEntity database operations
 * Nag-handle sa user authentication, account management, ug profile data
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    // Inherits: save(), findById(), findAll(), deleteById(), count(), etc.

    /**
     * FIND USER BY EMAIL ADDRESS / Pangitaa ang user gamit ang email address
     * SQL: SELECT * FROM tbl_profiles WHERE email = ?
     * Used in: User login, email verification, duplicate email check
     */
    Optional<ProfileEntity> findByEmail(String email);

    /**
     * FIND USER BY ACTIVATION TOKEN / Pangitaa ang user gamit ang activation token
     * SQL: SELECT * FROM tbl_profiles WHERE activation_token = ?
     * Used in: Account activation process, email verification links
     */
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}