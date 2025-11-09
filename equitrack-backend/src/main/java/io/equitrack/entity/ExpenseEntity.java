package io.equitrack.entity;

import jakarta.persistence.*;              // JPA annotations for database mapping
import lombok.*;                          // Lombok: reduces boilerplate code
import org.hibernate.annotations.CreationTimestamp;  // Auto-set creation timestamp
import org.hibernate.annotations.UpdateTimestamp;    // Auto-update modification timestamp
import java.math.BigDecimal;              // Precise decimal numbers for money
import java.time.LocalDate;               // Date without time (expense date)
import java.time.LocalDateTime;           // Date with time (audit timestamps)

/**
 * JPA Entity representing the 'expenses' table in the database
 * Stores all expense transactions with relationships to categories and users
 */
@Data                                    // Lombok: generates getters, setters, toString, etc.
@AllArgsConstructor                      // Lombok: constructor with all arguments
@NoArgsConstructor                       // Lombok: no-args constructor (JPA requirement)
@Builder                                 // Lombok: enables builder pattern
@Entity                                  // Marks this as JPA entity
@Table(name = "tbl_expenses")            // Maps to 'tbl_expenses' database table
public class ExpenseEntity {

    @Id                                  // Primary key field
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;                     // Unique identifier for each expense

    private String name;                 // Expense description: "Grocery Shopping", "Electric Bill"

    private String icon;                 // Visual icon: "🛒", "💡", "🚗"

    private LocalDate date;              // Date when expense occurred (YYYY-MM-DD)

    private BigDecimal amount;           // Expense amount - precise decimal for money

    @Column(updatable = false)           // Cannot be modified after creation
    @CreationTimestamp                   // Automatically set when record is created
    private LocalDateTime createdAt;     // Audit: when expense was recorded

    @UpdateTimestamp                     // Automatically updated on every change
    private LocalDateTime updatedAt;     // Audit: when expense was last modified

    // Relationship: Many expenses belong to one category
    @ManyToOne                           // Default FetchType.EAGER - loads category immediately
    @JoinColumn(name = "category_id", nullable = false) // Foreign key, required
    private CategoryEntity category;     // The category this expense belongs to

    // Relationship: Many expenses belong to one user profile
    @ManyToOne(fetch = FetchType.LAZY)   // Lazy loading for performance
    @JoinColumn(name = "profile_id", nullable = false) // Foreign key, required
    private ProfileEntity profile;       // The user who owns this expense

    /**
     * JPA Lifecycle callback - runs automatically before entity is persisted (saved)
     * Ensures every expense has a date, defaulting to today if not provided
     */
    @PrePersist
    public void prePersist(){
        if(this.date == null){
            this.date = LocalDate.now(); // Set to current date if not provided
        }
    }
}