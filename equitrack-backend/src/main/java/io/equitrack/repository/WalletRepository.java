package io.equitrack.repository;


import io.equitrack.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    // Find all wallets by profile ID
    List<WalletEntity> findByProfileId(Long profileId);

    // Find active wallets by profile ID
    List<WalletEntity> findByProfileIdAndIsActiveTrue(Long profileId);

    // Find wallet by profile and type
    Optional<WalletEntity> findByProfileIdAndWalletType(Long profileId, String walletType);

    // Check if wallet exists for profile
    boolean existsByProfileId(Long profileId);

    // Get total balance for a profile
    @Query("SELECT SUM(w.balance) FROM WalletEntity w WHERE w.profile.id = :profileId AND w.isActive = true")
    java.math.BigDecimal getTotalBalanceByProfileId(Long profileId);

    // Find wallets by currency
    List<WalletEntity> findByCurrency(String currency);
}