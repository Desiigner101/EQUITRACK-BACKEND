package io.equitrack.dto;

import lombok.AllArgsConstructor;    // Lombok: Java library that reduces boilerplate code
import lombok.Builder;              // Generates builder pattern methods for object creation
import lombok.Data;                 // Combines @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;    // Generates no-argument constructor

/**
 * Data Transfer Object for authentication operations
 * Handles login, registration, and token-based authentication data
 * Lombok: Automatically generates getters, setters, constructors at compile time
 * Eliminates the need to manually write repetitive boilerplate code
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDTO {

    // User's email address - used for login and account identification
    private String email;

    // User's password in plaintext - will be encrypted before storage
    private String password;

    // JWT token for authenticated requests or account activation tokens
    private String token;
}