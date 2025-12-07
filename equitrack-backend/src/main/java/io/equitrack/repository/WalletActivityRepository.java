package io.equitrack.repository;

import io.equitrack.entity.WalletActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletActivityRepository extends JpaRepository<WalletActivityEntity, Long> {

    /**
     * MODIFIED: Uses JOIN FETCH to ensure the 'wallet' object and its nested
     * 'walletType' are loaded eagerly along with the activity, resolving the
     * "Unknown Wallet" serialization error on the frontend.
     */
    @Query("SELECT wa FROM WalletActivityEntity wa JOIN FETCH wa.wallet w WHERE wa.profile.id = :profileId ORDER BY wa.createdAt DESC")
    List<WalletActivityEntity> findByProfileIdOrderByCreatedAtDesc(Long profileId);
}