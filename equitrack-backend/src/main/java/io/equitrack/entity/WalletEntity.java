package io.equitrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_wallets")
@Data
@AllArgsConstructor
@Builder
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference("profile-wallets")
    private ProfileEntity profile;

    // ✅ FIXED:
    // 1. cascade = CascadeType.ALL allows Deletion.
    // 2. @JsonIgnore prevents the "Unknown Wallet" infinite loop error.
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<WalletActivityEntity> activities = new ArrayList<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(length = 50)
    private String currency;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "wallet_type", length = 50)
    private String walletType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public WalletEntity() {
        this.balance = BigDecimal.ZERO;
        this.currency = "PHP";
        this.isActive = true;
        this.activities = new ArrayList<>();
    }

    public WalletEntity(ProfileEntity profile, BigDecimal balance, String walletType) {
        this.profile = profile;
        this.balance = balance;
        this.walletType = walletType;
        this.currency = "PHP";
        this.isActive = true;
        this.activities = new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- FULL MANUAL GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public List<WalletActivityEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<WalletActivityEntity> activities) {
        this.activities = activities;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- BUSINESS LOGIC METHODS ---

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 &&
                this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }

    public boolean canWithdraw(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
}