package io.equitrack.controller;


import io.equitrack.entity.WalletEntity;
import io.equitrack.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wallets")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // Create new wallet
    @PostMapping("/profile/{profileId}")
    public ResponseEntity<?> createWallet(
            @PathVariable Long profileId,
            @RequestBody Map<String, String> request) {
        try {
            String walletType = request.get("walletType");
            WalletEntity wallet = walletService.createWallet(profileId, walletType);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all wallets for a profile
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<WalletEntity>> getWalletsByProfile(@PathVariable Long profileId) {
        List<WalletEntity> wallets = walletService.getWalletsByProfile(profileId);
        return ResponseEntity.ok(wallets);
    }

    // Get active wallets
    @GetMapping("/profile/{profileId}/active")
    public ResponseEntity<List<WalletEntity>> getActiveWallets(@PathVariable Long profileId) {
        List<WalletEntity> wallets = walletService.getActiveWallets(profileId);
        return ResponseEntity.ok(wallets);
    }

    // Get wallet by ID
    @GetMapping("/{walletId}")
    public ResponseEntity<?> getWalletById(@PathVariable Long walletId) {
        return walletService.getWalletById(walletId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Deposit money
    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable Long walletId,
            @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal amount = request.get("amount");
            WalletEntity wallet = walletService.deposit(walletId, amount);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Withdraw money
    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable Long walletId,
            @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal amount = request.get("amount");
            WalletEntity wallet = walletService.withdraw(walletId, amount);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Transfer between wallets
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> request) {
        try {
            Long fromWalletId = Long.valueOf(request.get("fromWalletId").toString());
            Long toWalletId = Long.valueOf(request.get("toWalletId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            walletService.transfer(fromWalletId, toWalletId, amount);
            return ResponseEntity.ok("Transfer successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get total balance
    @GetMapping("/profile/{profileId}/total-balance")
    public ResponseEntity<Map<String, BigDecimal>> getTotalBalance(@PathVariable Long profileId) {
        BigDecimal total = walletService.getTotalBalance(profileId);
        return ResponseEntity.ok(Map.of("totalBalance", total));
    }

    // Update wallet
    @PutMapping("/{walletId}")
    public ResponseEntity<?> updateWallet(
            @PathVariable Long walletId,
            @RequestBody WalletEntity updatedWallet) {
        try {
            WalletEntity wallet = walletService.updateWallet(walletId, updatedWallet);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Deactivate wallet
    @PatchMapping("/{walletId}/deactivate")
    public ResponseEntity<?> deactivateWallet(@PathVariable Long walletId) {
        try {
            WalletEntity wallet = walletService.deactivateWallet(walletId);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete wallet
    @DeleteMapping("/{walletId}")
    public ResponseEntity<?> deleteWallet(@PathVariable Long walletId) {
        try {
            walletService.deleteWallet(walletId);
            return ResponseEntity.ok("Wallet deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}