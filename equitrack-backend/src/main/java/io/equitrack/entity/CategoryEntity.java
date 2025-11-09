package io.equitrack.entity;

import jakarta.persistence.*;        // JPA annotations for database mapping
import lombok.*;                    // Lombok: reduces boilerplate code
import org.hibernate.annotations.CreationTimestamp;  // Auto-set creation timestamp
import org.hibernate.annotations.UpdateTimestamp;    // Auto-update modification timestamp
import java.time.LocalDateTime;

/**
 * JPA Entity representing the 'categories' table in the database
 * Stores transaction categories for organizing incomes and expenses
 */
@Entity                            // Marks this as JPA entity - maps to database table
@Getter                            // Lombok: generates getters for all fields
@Setter                            // Lombok: generates setters for all fields
@Table(name = "tbl_categories")    // Specifies the actual database table name
@AllArgsConstructor               // Lombok: generates constructor with all arguments
@NoArgsConstructor                // Lombok: generates no-args constructor (JPA requirement)
@Builder                          // Lombok: enables builder pattern for object creation
public class CategoryEntity {

    @Id                            // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID (MySQL/Auto)
    private Long id;               // Unique identifier for each category

    private String name;           // Category name: "Food", "Transport", "Salary", etc.

    @Column(updatable = false)     // This field cannot be updated after initial creation
    @CreationTimestamp             // Hibernate: automatically sets timestamp on creation
    private LocalDateTime createdAt; // When this category was created

    @UpdateTimestamp               // Hibernate: automatically updates on every modification
    private LocalDateTime updatedAt; // When this category was last updated

    private String type;           // "income" or "expense" - determines category grouping

    private String icon;           // Visual representation: "🍔", "🚗", "💰" or icon class

    // Relationship: Many categories belong to one profile
    // Many categories → One profile
    @ManyToOne(fetch = FetchType.LAZY)      // Lazy loading for performance optimization
    @JoinColumn(name = "profile_id", nullable = false) // Foreign key column in database
    private ProfileEntity profile;          // The user who owns this category
}