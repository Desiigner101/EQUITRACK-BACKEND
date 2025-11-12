package io.equitrack.dto;

import lombok.AllArgsConstructor;    // Lombok: Generates constructor with all fields
import lombok.Builder;              // Lombok: Enables builder pattern for object creation
import lombok.Data;                 // Lombok: Auto-generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;    // Lombok: Generates empty constructor for JSON deserialization

import java.math.BigDecimal;        // Precise decimal numbers for monetary values
import java.time.LocalDate;         // Date without time (for transaction date)
import java.time.LocalDateTime;     // Date with time (for audit timestamps)

/**
 * Data Transfer Object for recent transaction displays
 * Unified view of both income and expense transactions for dashboard and recent activity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentTransactionDTO {

    // Unique database identifier for the transaction
    private Long id;

    // User who owns this transaction - ensures data isolation
    private Long profileId;

    // Visual icon representation for quick recognition
    private String icon;

    // Transaction description or title
    private String name;

    // Transaction amount (positive for income, negative for expense in display logic)
    private BigDecimal amount;

    // Date when the transaction occurred
    private LocalDate date;

    // Timestamp when transaction record was created
    private LocalDateTime createdAt;

    // Timestamp when transaction record was last modified
    private LocalDateTime updatedAt;

    // Transaction type: "income" or "expense" - determines display styling
    private String type;
}