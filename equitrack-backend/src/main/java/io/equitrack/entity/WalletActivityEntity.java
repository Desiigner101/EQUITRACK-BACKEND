package io.equitrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // ✅ CRITICAL IMPORT
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_wallet_activities")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the Wallet involved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    // ✅ CRITICAL FIX: Removed @JsonBackReference.
    // Added @JsonIgnoreProperties to stop the wallet from re-loading its activities (Infinite Loop)
    @JsonIgnoreProperties({"activities", "profile"})
    private WalletEntity wallet;

    // Link back to the Profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference("profile-activities")
    private ProfileEntity profile;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // Type: DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN
    @Column(name = "activity_type", length = 20, nullable = false)
    private String type;

    // Optional: ID of the other wallet in case of a transfer
    @Column(name = "related_wallet_id")
    private Long relatedWalletId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}