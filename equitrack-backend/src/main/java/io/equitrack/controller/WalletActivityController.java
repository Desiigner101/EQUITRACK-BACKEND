package io.equitrack.controller;

import io.equitrack.entity.WalletActivityEntity; // NEW Import: Entity to be returned
import io.equitrack.repository.WalletActivityRepository; // NEW Import: Repository to fetch data
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions") // Base path: maps to /api/v1.0/transactions
@CrossOrigin(origins = "*")
public class WalletActivityController {

    // Inject the repository to access the transaction log data
    @Autowired
    private WalletActivityRepository walletActivityRepository;

    /**
     * GET /api/v1.0/transactions/profile/{profileId}
     * Fetches all wallet activities for a given profile, ordered by date descending.
     * This fulfills the requirement for the frontend's Transaction History.
     * * @param profileId The ID of the profile
     * @return A list of WalletActivityEntity objects
     */
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<WalletActivityEntity>> getActivitiesByProfile(
            @PathVariable Long profileId) {

        // Use the custom repository method to fetch activities, sorted by newest first
        List<WalletActivityEntity> activities =
                walletActivityRepository.findByProfileIdOrderByCreatedAtDesc(profileId);

        return ResponseEntity.ok(activities);
    }
}