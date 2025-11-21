package io.equitrack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_budgets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;  // Whose budget is this?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;  // Budget for which category?

    @Column(nullable = false)
    private BigDecimal limitAmount;  // Max spending amount

    @Column(nullable = false)
    private String period;  // "MONTHLY" or "WEEKLY"

    private String description;  // Optional: "Groceries budget for family"

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}