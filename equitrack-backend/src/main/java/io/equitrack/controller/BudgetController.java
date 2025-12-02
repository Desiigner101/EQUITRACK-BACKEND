package io.equitrack.controller;

import io.equitrack.entity.BudgetEntity;
import io.equitrack.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    // Create new budget
    @PostMapping("/profile/{profileId}")
    public ResponseEntity<?> createBudget(
            @PathVariable Long profileId,
            @RequestBody Map<String, Object> request) {
        try {
            Long categoryId = Long.valueOf(request.get("categoryId").toString());
            BigDecimal limitAmount = new BigDecimal(request.get("limitAmount").toString());
            String period = request.get("period").toString();
            String description = request.get("description") != null ? request.get("description").toString() : null;

            BudgetEntity budget = budgetService.createBudget(profileId, categoryId, limitAmount, period, description);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get all budgets for a profile
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<BudgetEntity>> getBudgetsByProfile(@PathVariable Long profileId) {
        try {
            List<BudgetEntity> budgets = budgetService.getBudgetsByProfile(profileId);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get budgets by period
    @GetMapping("/profile/{profileId}/period/{period}")
    public ResponseEntity<?> getBudgetsByPeriod(
            @PathVariable Long profileId,
            @PathVariable String period) {
        try {
            List<BudgetEntity> budgets = budgetService.getBudgetsByPeriod(profileId, period);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get budget by ID
    @GetMapping("/{budgetId}")
    public ResponseEntity<?> getBudgetById(@PathVariable Long budgetId) {
        return budgetService.getBudgetById(budgetId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update budget
    @PutMapping("/{budgetId}")
    public ResponseEntity<?> updateBudget(
            @PathVariable Long budgetId,
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal limitAmount = request.get("limitAmount") != null
                    ? new BigDecimal(request.get("limitAmount").toString())
                    : null;
            String period = request.get("period") != null
                    ? request.get("period").toString()
                    : null;
            String description = request.get("description") != null
                    ? request.get("description").toString()
                    : null;

            BudgetEntity budget = budgetService.updateBudget(budgetId, limitAmount, period, description);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Delete budget
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long budgetId) {
        try {
            budgetService.deleteBudget(budgetId);
            return ResponseEntity.ok(Map.of("message", "Budget deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}