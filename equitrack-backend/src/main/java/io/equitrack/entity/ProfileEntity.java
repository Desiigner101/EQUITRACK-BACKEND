package io.equitrack.entity;

import jakarta.persistence.*;              // JPA annotations for database mapping
import lombok.*;                          // Lombok: reduces boilerplate code
import org.hibernate.annotations.CreationTimestamp;  // Auto-set creation timestamp
import org.hibernate.annotations.UpdateTimestamp;    // Auto-update modification timestamp
import java.time.LocalDateTime;           // Date with time (audit timestamps)

/**
 * JPA Entity representing the 'tbl_profiles' table in the database
 * Central entity for user management, authentication, and account status
 * This is the ROOT entity that owns all other user data in the system
 */
@Entity                                  // Marks this class as a JPA entity
@Table(name = "tbl_profiles")            // Maps to 'tbl_profiles' table in database
@Data                                    // Lombok: generates getters, setters, toString, equals, hashCode
@AllArgsConstructor                      // Lombok: constructor with all fields
@NoArgsConstructor                       // Lombok: empty constructor (JPA requirement)
@Builder                                 // Lombok: enables builder pattern
public class ProfileEntity {

    @Id                                  // Primary key field
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;                     // Unique identifier for each user

    private String fullName;             // User's display name: "John Doe", "Jane Smith"

    @Column(unique = true)               // Database-level uniqueness constraint
    private String email;                // User's email - serves as username (UNIQUE in database)

    private String password;             // Encrypted password (BCrypt hash)

    private String profileImageUrl;      // Optional: URL to user's avatar/profile picture

    @Column(updatable = false)           // Cannot be modified after creation
    @CreationTimestamp                   // Auto-set when user registers
    private LocalDateTime createdAt;     // When user account was created

    @UpdateTimestamp                     // Auto-updated on every profile change
    private LocalDateTime updatedAt;     // When profile was last modified

    private Boolean isActive;            // Account activation status: false=inactive, true=active

    private String activationToken;      // Unique token for email verification

    /**
     * JPA Lifecycle Callback - runs automatically before entity is persisted
     * Ensures new users are created as inactive until they verify their email
     */
    @PrePersist
    public void prePersist(){
        // Default new users to inactive (email not verified)
        if(this.isActive == null){
            this.isActive = false; // User must activate via email verification
        }
    }
}