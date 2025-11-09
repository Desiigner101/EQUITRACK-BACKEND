package io.equitrack.entity;

import jakarta.persistence.*;              // JPA annotations for database mapping
import lombok.*;                          // Lombok: reduces boilerplate code
import org.hibernate.annotations.CreationTimestamp;  // Auto-set creation timestamp
import org.hibernate.annotations.UpdateTimestamp;    // Auto-update modification timestamp
import java.math.BigDecimal;              // Precise decimal numbers for money
import java.time.LocalDate;               // Date without time (income date)
import java.time.LocalDateTime;           // Date with time (audit timestamps)

/**
 * JPA Entity representing the 'tbl_incomes' table in the database
 * Stores all income transactions with relationships to categories and users
 * This is the counterpart to ExpenseEntity, handling money inflow instead of outflow
 */
@Data                                    // Lombok: generates getters, setters, toString, equals, hashCode
@AllArgsConstructor                      // Lombok: constructor with all fields (id, name, icon, date, amount, etc.)
@NoArgsConstructor                       // Lombok: empty constructor (REQUIRED by JPA for entity loading)
@Builder                                 // Lombok: enables builder pattern for easy object creation
@Entity                                  // Marks this class as a JPA entity - will be mapped to a database table
@Table(name = "tbl_incomes")             // Specifies the exact table name in the database
public class IncomeEntity {

    @Id                                  // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Database automatically generates ID (auto-increment)
    private Long id;                     // Unique identifier for each income record

    private String name;                 // Income source description: "Salary", "Freelance Work", "Investment Returns"

    private String icon;                 // Visual representation: "💰", "💼", "📈" (emoji or icon class)

    private LocalDate date;              // Date when income was received (just date, no time)

    private BigDecimal amount;           // Income amount - uses BigDecimal for precise financial calculations

    @Column(updatable = false)           // This field cannot be updated after initial creation
    @CreationTimestamp                   // Hibernate automatically sets this timestamp when entity is first saved
    private LocalDateTime createdAt;     // Audit trail: when this income record was created

    @UpdateTimestamp                     // Hibernate automatically updates this timestamp on every save/update
    private LocalDateTime updatedAt;     // Audit trail: when this income record was last modified

    // Relationship: Many incomes can belong to one category
    @ManyToOne(fetch = FetchType.LAZY)   // Lazy loading - category data loaded only when specifically accessed
    @JoinColumn(name = "category_id", nullable = false) // Foreign key column in database, required field
    private CategoryEntity category;     // The category this income belongs to (e.g., "Salary", "Business")

    // Relationship: Many incomes can belong to one user profile
    @ManyToOne(fetch = FetchType.LAZY)   // Lazy loading - profile data loaded only when specifically accessed
    @JoinColumn(name = "profile_id", nullable = false) // Foreign key column in database, required field
    private ProfileEntity profile;       // The user who owns this income record

    /**
     * JPA Lifecycle Callback Method - Executed automatically before entity is persisted (saved to database)
     * Ensures data consistency by providing default values for required fields
     */
    @PrePersist
    public void prePersist(){
        // If no date is provided when creating the income, default to today's date
        if(this.date == null){
            this.date = LocalDate.now(); // Sets the income date to current date
        }
    }
}