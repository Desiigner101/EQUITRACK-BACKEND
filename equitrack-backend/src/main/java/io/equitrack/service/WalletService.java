package io.equitrack.service;

import io.equitrack.entity.ProfileEntity;
import io.equitrack.entity.WalletEntity;
import io.equitrack.entity.WalletActivityEntity; // NEW Import: Entity for logging
import io.equitrack.repository.ProfileRepository;
import io.equitrack.repository.WalletRepository;
import io.equitrack.repository.WalletActivityRepository; // NEW Import: Repository for logging
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Wallet operations
 * Handles all business logic for wallet management, deposits, withdrawals, and transfers
 */
@Service
@Transactional
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final ProfileRepository profileRepository;
    private final WalletActivityRepository walletActivityRepository; // NEW: Inject Wallet Activity Repository

    /**
     * Creates a new wallet for a profile
     * @param profileId The ID of the profile
     * @param walletType Type of wallet (MAIN, SAVINGS, INVESTMENT, EMERGENCY)
     * @return The created wallet entity
     * @throws RuntimeException if profile not found or wallet type already exists
     */
    public WalletEntity createWallet(Long profileId, String walletType) {
        // Find the profile or throw exception
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + profileId));

        // Check if wallet type already exists for this profile
        Optional<WalletEntity> existing = walletRepository
                .findByProfileIdAndWalletType(profileId, walletType);

        if (existing.isPresent()) {
            throw new RuntimeException("Wallet type '" + walletType + "' already exists for this profile");
        }

        // Create new wallet using builder pattern
        WalletEntity wallet = WalletEntity.builder()
                .profile(profile)
                .balance(BigDecimal.ZERO)
                .walletType(walletType)
                .currency("PHP")
                .isActive(true)
                .build();

        return walletRepository.save(wallet);
    }

    /**
     * Get all wallets for a specific profile
     * @param profileId The profile ID
     * @return List of all wallets (active and inactive)
     */
    public List<WalletEntity> getWalletsByProfile(Long profileId) {
        return walletRepository.findByProfileId(profileId);
    }

    /**
     * Get only active wallets for a profile
     * @param profileId The profile ID
     * @return List of active wallets only
     */
    public List<WalletEntity> getActiveWallets(Long profileId) {
        return walletRepository.findByProfileIdAndIsActiveTrue(profileId);
    }

    /**
     * Get a specific wallet by ID
     * @param walletId The wallet ID
     * @return Optional containing the wallet if found
     */
    public Optional<WalletEntity> getWalletById(Long walletId) {
        return walletRepository.findById(walletId);
    }

    /**
     * Deposit money into a wallet
     * @param walletId The wallet ID
     * @param amount Amount to deposit (must be positive)
     * @return Updated wallet entity
     * @throws RuntimeException if wallet not found or amount is invalid
     */
    public WalletEntity deposit(Long walletId, BigDecimal amount) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        if (!wallet.getIsActive()) {
            throw new RuntimeException("Cannot deposit to inactive wallet");
        }

        wallet.deposit(amount);
        WalletEntity updatedWallet = walletRepository.save(wallet);

        // NEW: Record the DEPOSIT activity log
        WalletActivityEntity depositActivity = WalletActivityEntity.builder()
                .wallet(updatedWallet)
                .profile(updatedWallet.getProfile())
                .amount(amount)
                .type("DEPOSIT")
                .build();
        walletActivityRepository.save(depositActivity);

        return updatedWallet;
    }

    /**
     * Withdraw money from a wallet
     * @param walletId The wallet ID
     * @param amount Amount to withdraw
     * @return Updated wallet entity
     * @throws RuntimeException if wallet not found, insufficient balance, or invalid amount
     */
    public WalletEntity withdraw(Long walletId, BigDecimal amount) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        if (!wallet.getIsActive()) {
            throw new RuntimeException("Cannot withdraw from inactive wallet");
        }

        if (!wallet.withdraw(amount)) {
            throw new RuntimeException("Insufficient balance. Available: " + wallet.getBalance() + ", Requested: " + amount);
        }

        WalletEntity updatedWallet = walletRepository.save(wallet);

        // NEW: Record the WITHDRAW activity log
        WalletActivityEntity withdrawActivity = WalletActivityEntity.builder()
                .wallet(updatedWallet)
                .profile(updatedWallet.getProfile())
                .amount(amount.negate()) // Store as negative for clear history tracking
                .type("WITHDRAW")
                .build();
        walletActivityRepository.save(withdrawActivity);

        return updatedWallet;
    }

    /**
     * Transfer money between two wallets
     * @param fromWalletId Source wallet ID
     * @param toWalletId Destination wallet ID
     * @param amount Amount to transfer
     * @throws RuntimeException if wallets not found or insufficient balance
     */
    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }

        // Get both wallets
        WalletEntity fromWallet = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Source wallet not found with ID: " + fromWalletId));

        WalletEntity toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found with ID: " + toWalletId));

        // Validate wallets are active
        if (!fromWallet.getIsActive()) {
            throw new RuntimeException("Source wallet is inactive");
        }
        if (!toWallet.getIsActive()) {
            throw new RuntimeException("Destination wallet is inactive");
        }

        // Perform transfer
        if (!fromWallet.withdraw(amount)) {
            throw new RuntimeException("Insufficient balance in source wallet. Available: " +
                    fromWallet.getBalance() + ", Requested: " + amount);
        }

        toWallet.deposit(amount);

        // Save both wallets
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // NEW: Record two activities for the transfer (OUT and IN)

        // 1. Record TRANSFER_OUT from source wallet
        WalletActivityEntity transferOutActivity = WalletActivityEntity.builder()
                .wallet(fromWallet)
                .profile(fromWallet.getProfile())
                .amount(amount.negate())
                .type("TRANSFER_OUT")
                .relatedWalletId(toWalletId)
                .build();
        walletActivityRepository.save(transferOutActivity);

        // 2. Record TRANSFER_IN to destination wallet
        WalletActivityEntity transferInActivity = WalletActivityEntity.builder()
                .wallet(toWallet)
                .profile(toWallet.getProfile())
                .amount(amount)
                .type("TRANSFER_IN")
                .relatedWalletId(fromWalletId)
                .build();
        walletActivityRepository.save(transferInActivity);
    }

    /**
     * Calculate total balance across all active wallets for a profile
     * @param profileId The profile ID
     * @return Total balance in BigDecimal
     */
    public BigDecimal getTotalBalance(Long profileId) {
        BigDecimal total = walletRepository.getTotalBalanceByProfileId(profileId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Deactivate a wallet (soft delete)
     * @param walletId The wallet ID
     * @return Updated wallet entity
     * @throws RuntimeException if wallet not found
     */
    public WalletEntity deactivateWallet(Long walletId) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        wallet.setIsActive(false);
        return walletRepository.save(wallet);
    }

    /**
     * Activate a previously deactivated wallet
     * @param walletId The wallet ID
     * @return Updated wallet entity
     * @throws RuntimeException if wallet not found
     */
    public WalletEntity activateWallet(Long walletId) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        wallet.setIsActive(true);
        return walletRepository.save(wallet);
    }

    /**
     * Update wallet properties (currency, wallet type)
     * Note: Balance cannot be updated directly, use deposit/withdraw instead
     * @param walletId The wallet ID
     * @param updatedWallet Wallet entity with updated values
     * @return Updated wallet entity
     * @throws RuntimeException if wallet not found
     */
    public WalletEntity updateWallet(Long walletId, WalletEntity updatedWallet) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        // Update only allowed fields
        if (updatedWallet.getCurrency() != null) {
            wallet.setCurrency(updatedWallet.getCurrency());
        }
        if (updatedWallet.getWalletType() != null) {
            // Check if new wallet type already exists for this profile
            Optional<WalletEntity> existing = walletRepository
                    .findByProfileIdAndWalletType(wallet.getProfile().getId(), updatedWallet.getWalletType());

            if (existing.isPresent() && !existing.get().getId().equals(walletId)) {
                throw new RuntimeException("Wallet type '" + updatedWallet.getWalletType() +
                        "' already exists for this profile");
            }
            wallet.setWalletType(updatedWallet.getWalletType());
        }

        return walletRepository.save(wallet);
    }

    /**
     * Delete a wallet permanently
     * @param walletId The wallet ID
     * @throws RuntimeException if wallet not found
     */
    public void deleteWallet(Long walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new RuntimeException("Wallet not found with ID: " + walletId);
        }
        // NOTE: The actual cascade deletion of WalletActivityEntity records is now
        // handled automatically by the configuration in WalletEntity.java.

        walletRepository.deleteById(walletId);
    }

    /**
     * Check if a profile has any wallets
     * @param profileId The profile ID
     * @return true if profile has at least one wallet
     */
    public boolean hasWallets(Long profileId) {
        return walletRepository.existsByProfileId(profileId);
    }

    /**
     * Get wallet by profile ID and wallet type
     * @param profileId The profile ID
     * @param walletType The wallet type
     * @return Optional containing the wallet if found
     */
    public Optional<WalletEntity> getWalletByProfileAndType(Long profileId, String walletType) {
        return walletRepository.findByProfileIdAndWalletType(profileId, walletType);
    }
}